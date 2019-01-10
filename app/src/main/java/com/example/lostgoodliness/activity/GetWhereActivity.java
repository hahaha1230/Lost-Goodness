package com.example.lostgoodliness.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.lostgoodliness.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class GetWhereActivity extends AppCompatActivity implements AMap.OnMapClickListener,
        View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener {

    private AMap aMap;
    private MapView mapView;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Marker centerMarker;
    private ProgressDialog dialog;
    private UiSettings uiSettings;
    private TextView addressInfoTv;
    private TextView backTv;
    private TextView sureTv;
    private TextView baseTitle;
    //记录所点击的城市
    private String city;
    //记录所点击的具体信息；
    private String addressInfo;
    //卷积算法进十级房
    //记录所点击的经度
    private double longitude;
    //记录所点击的纬度
    private double latitude;
    //记录是否第一次定位成功
    private boolean firstLocation = true;
    //用于逆编码查询的tool
    private GeocodeSearch geocodeSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_lost_where);

        mapView = (MapView) findViewById(R.id.getWhere);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        //地理搜索类
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);


        initView();
        getRight();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        if (aMap == null) {
            aMap = mapView.getMap();
            uiSettings = aMap.getUiSettings();
        }
        View view = (View) findViewById(R.id.customTitle);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle = (TextView) view.findViewById(R.id.base_tv_title);
        addressInfoTv = (TextView) findViewById(R.id.addressInfoTv);
        baseTitle.setText("Select Where");


        backTv.setOnClickListener(this);
        sureTv.setOnClickListener(this);
        aMap.setOnMapClickListener(this);
    }


    /**
     * 检查是否缺少定位权限
     * 缺少就去申请，不缺就去定位
     */
    private void getRight() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            //缺少权限去申请
            ActivityCompat.requestPermissions(GetWhereActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else { //有权限去定位
            initLocation();
            startLocation();
        }
    }


    /**
     * 请求权限结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //成功获取到权限去定位
                    initLocation();
                    startLocation();
                } else {
                    Toast.makeText(GetWhereActivity.this, "没有开启定位权限,无法定位"
                            , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 一些控件点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_tv_back:
                finish();
                break;
            case R.id.base_tv_right_btn:
//                if (addressInfoTv.getText().toString().isEmpty()){
//                    Toast.makeText(GetWhereActivity.this,"请先点击地图获取位置哦",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString("city", city);
                bundle.putString("addressInfo", addressInfo);
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 初始化定位参数
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);

        MyLocationStyle myLocationStyle = new MyLocationStyle();

        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(3000);
        aMap.setMyLocationStyle(myLocationStyle);
        uiSettings.setZoomControlsEnabled(true);

        //显示指南针
        uiSettings.setCompassEnabled(true);
        //显示比例尺
        uiSettings.setScaleControlsEnabled(true);
        //是否显示默认的定位按钮
        uiSettings.setMyLocationButtonEnabled(true);

        aMap.setMyLocationEnabled(true);
    }

    /**
     * 开启定位
     */
    private void startLocation() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(true);
        // 设置是否单次定位
        locationOption.setOnceLocation(false);
        //设置是否使用传感器
        locationOption.setSensorEnable(true);
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        //添加定位监听
        locationClient.setLocationListener(locationListener);
        // 启动定位
        locationClient.startLocation();
    }


    /**
     * 添加定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (firstLocation) {
                LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                Log.d("hhh", "经纬度分别是" + aMapLocation.getLatitude() + aMapLocation.getLongitude());
                //getAddressInfo(latLng);
                getAddressByLatlng(latLng);
                firstLocation = false;
            }
        }
    };


    /**
     * 地图的点击事件
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        if (centerMarker != null) {
            //marker存在就先清除
            centerMarker.remove();
        }
        //添加marker
        MarkerOptions markerOptions = new MarkerOptions().icon
                (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.position(latLng);
        centerMarker = aMap.addMarker(markerOptions);
        //getAddressInfo(latLng);
        getAddressByLatlng(latLng);
    }


    /**
     * 根据经纬度获取具体的位置信息
     * （经过测试，这个方法在一些android手机版本比较低的手机上
     * 获取不到结果，所以，这里调用高德逆编码查询进行代替该方法）
     */
    private void getAddressInfo(final LatLng latLng) {
        if (dialogIsShow()) {
            return;
        }
        showDialog();
        //开启线程请求位置信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Geocoder geocoder = new Geocoder(GetWhereActivity.this, Locale.getDefault());
                    List addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.size() > 0) {
                        String addressInfo = addresses.get(0).toString();
                        int startCity = addressInfo.indexOf("locality=") + "locality=".length();
                        int endCity = addressInfo.indexOf(",", startCity);
                        city = addressInfo.substring(startCity, endCity);
                        int startPlace = addressInfo.indexOf("feature=") + "feature=".length();
                        int endPlace = addressInfo.indexOf(",", startPlace);
                        String place = addressInfo.substring(startPlace, endPlace);
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = city + place;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = "查询出错" + e.getMessage();
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }


    /**
     * handler接收信息
     */

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                dismissDialog();
                addressInfo = (String) msg.obj;
                addressInfoTv.setText(addressInfo);
            }
            return true;
        }
    });

    /**
     * 根据 latlng 进行逆编码查询
     * @param latLng
     */
    private void getAddressByLatlng(LatLng latLng) {
        showDialog();
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }


    /**
     * 不知道是什么回调
     *
     * @param geocodeResult
     * @param i
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }


    /**
     * 得到逆地理编码异步查询结果
     * （异步逆编码查询成功后回调该方法）
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        dismissDialog();
        String simpleAddress;
        String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        city = regeocodeResult.getRegeocodeAddress().getCity();
        addressInfo = formatAddress;
        addressInfoTv.setText(formatAddress);
    }


    /**
     * 显示dialog
     */
    private void showDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("获取位置信息中...");
        }
        dialog.show();
    }


    /**
     * 消失dialog
     */
    private void dismissDialog() {
        if (dialogIsShow()) {
            dialog.dismiss();
        }
    }

    /**
     * 判断dialog是否正在显示
     *
     * @return
     */
    private boolean dialogIsShow() {
        return dialog != null && dialog.isShowing();
    }


    /**
     * 初始化定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        destroyLocation();
    }

    /**
     * 方法必须重写
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }


}
