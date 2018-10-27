package com.example.lostgoodliness.activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.FoundTable;
import com.example.lostgoodliness.javabean.LostTable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class GoodsDetailsInfoActivity extends AppCompatActivity implements AMapLocationListener {
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    //声明mlocationClient对象
    private AMapLocationClient mlocationClient = null;
    //重新进行定位获取当前的经纬度
    private double nowLatitude;
    private double nowLongitude;
    //记录数据中丢失或捡到物品的坐标信息
    private double latitude;
    private double longitude;
    private TextView detailsInfo;
    private TextView sureTv;
    private TextView backTv;
    private TextView goodsNoImage;
    private TextView baseTitle;
    private ImageView goodsImage;
    private LostTable lostTable;
    private FoundTable foundTable;
    private String allInfo = null;
    private boolean isLost = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details_info);

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        if (this.getIntent().getStringExtra("type").equals("lost")) {
            isLost = true;
        } else {
            isLost = false;
        }
        if (isLost) {
            lostTable = (LostTable) this.getIntent().getSerializableExtra("lostInfo");
        } else {
            foundTable = (FoundTable) this.getIntent().getSerializableExtra("foundInfo");
        }

        initData();

        initView();
    }


    /**
     * 初始化界面
     */
    private void initView() {
        View view = (View) findViewById(R.id.customTitle);
        backTv = (TextView) view.findViewById(R.id.base_tv_back);
        sureTv = (TextView) view.findViewById(R.id.base_tv_right_btn);
        baseTitle = (TextView) view.findViewById(R.id.base_tv_title);
        detailsInfo = (TextView) findViewById(R.id.details_info_text);
        goodsImage = (ImageView) findViewById(R.id.goods_image);
        goodsNoImage = (TextView) findViewById(R.id.goods_no_image);

        baseTitle.setText("详细信息");
        sureTv.setVisibility(View.GONE);
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //给imageview设置旋转动画
        Animation rotateAnimation= AnimationUtils.loadAnimation(this,R.anim.anim_circle_rotate);
        LinearInterpolator interpolator=new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);
        goodsImage.startAnimation(rotateAnimation);


        //当前界面时显示丢失信息
        if (isLost) {
            allInfo = lostTable.getUserName() + "于" + lostTable.getLostTime() + "在" + lostTable.getLostWhere()
                    + "丢失了一个" + lostTable.getLostType() + "。详细信息为：" + lostTable.getGoodsDescribe();
            detailsInfo.setText(allInfo);

            //显示物品图片
            setImage(lostTable.getLostGoodImage());

        } else { //当前界面要显示的是捡到信息
            allInfo = foundTable.getUserName() + "于" + foundTable.getFindTime() + "在" + foundTable.getFindWhere()
                    + "捡到了一个" + foundTable.getFindType() + "。详细信息为：" + foundTable.getGoodDescribe();
            detailsInfo.setText(allInfo);

            //显示物品图片
            setImage(foundTable.getFoundGoodImage());

        }

    }


    /**
     * 显示图片
     *
     * @param imageUri
     */
    private void setImage(String imageUri) {
        if (imageUri != null) {
            ImageLoader.getInstance().loadImage(imageUri, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                   //关闭旋转动画
                    goodsImage.clearAnimation();
                    goodsImage.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            goodsImage.setVisibility(View.GONE);
            goodsNoImage.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 初始化定位参数
     */
    private void initData() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(3000);
        //设置单次定位
        // mLocationOption.setOnceLocation(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }


    /**
     * 定位回调，每次定位成功后都会调用该方法
     * (如果每次点击都会进行定位，这个过程会很费电，也浪费资源，也可以从上个界面
     * 进行定位，然后把经纬度传入进来)
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("hhh", "goods details info中定位成功");
        //如果定位成功后就不用再定位
        if (!aMapLocation.getCity().isEmpty()) {
            nowLatitude = aMapLocation.getLatitude();
            nowLongitude = aMapLocation.getLongitude();
            //停止定位
            mlocationClient.stopLocation();


            LatLng nowLocation = new LatLng(nowLatitude, nowLongitude);
            LatLng lostWhere = null;
            LatLng foundWhere = null;
            try {
                latitude = foundTable.getLatitude();
                longitude = foundTable.getLongitude();
                if (latitude==0.0||longitude==0.0){
                    return;
                }
                foundWhere = new LatLng(latitude, longitude);
                //计算捡到地方与当前地方的距离
                double distance = AMapUtils.calculateLineDistance
                        (foundWhere, nowLocation);
                allInfo += ";距离您当前位置大约是" + distance + "米";
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                latitude = lostTable.getLatitude();
                longitude = lostTable.getLongitude();
                if (latitude==0.0||longitude==0.0){
                    return;
                }
                lostWhere = new LatLng(latitude, longitude);
                //计算丢失地方与当前地方的距离
                double distance = AMapUtils.calculateLineDistance
                        (lostWhere, nowLocation);
                allInfo += ";距离您当前位置大约是" + (int)distance + "米";
            } catch (Exception e) {
                e.printStackTrace();
            }
            detailsInfo.setText(allInfo);
        }
    }



    /**
     * 如果AMapLocationClient是在当前Activity实例化的，
     * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
            mlocationClient = null;
            mLocationOption = null;
        }
    }
}
