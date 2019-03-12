package com.example.lostgoodliness.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.example.lostgoodliness.R;
import com.example.lostgoodliness.adapter.HomeFragmentPagerAdapter;
import com.example.lostgoodliness.fragment.RecommendContentFragment;
import com.example.lostgoodliness.handler.MessageHandler;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.view.NavigationMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements AMapLocationListener,
        WeatherSearch.OnWeatherSearchListener, View.OnClickListener {

    //需要进行检测的权限数组
    private String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    //记录需要申请哪些权限
    private List<String> permissionList = new ArrayList<>();
    //fragment列表
    private List<Fragment> fragmentList=new ArrayList<>();
    //声明mLocationOption对象
    private AMapLocationClientOption mLocationOption = null;
    //声明mlocationClient对象
    private AMapLocationClient mlocationClient = null;

    public static final int REQUEST_RIGHT = 0;
    public static final int RESULT_FROM_ALBUM = 1;
    public static final int RESULT_FROM_CAMERA = 2;
    public static final int CUT_PHOTO = 3;
    private NavigationMenu mNavigationMenu;
    private ViewPager viewPager;
    private WeatherSearchQuery mquery;                 //天气查询
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private DrawerLayout mDrawerLayout;
    private NavigationView navView;
    private CircleImageView userIcon;
    private ProgressDialog progressDialog;
    private TextView baseTitle;
    private TextView weatherTv;
    private TextView monthTv;
    private TextView dataTv;
    private TextView userName;
    private boolean isPressedBackOnce = false;       //记录是否点击过一次back
    private long firstPressedTime = 0;               //记录第一次点击back时间
    private long secondPressedTime = 0;              //记录第二次点击back时间
    public static Users user;
    private String phone;                            //记录手机号
    private String weatherNow;                       //记录所在城市的天气
    private String city;                             //记录所在城市
    private String imagePath = null;                 //记录选择图片的路径
    private File mFile;
    private Bitmap mBitmap;
    public static boolean isReceivePush=true;        //记录用户是否想接收推送



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /**
         * 这里再次进行初始化一次bmob，按说不需要再初始化，但是测试时候
         * 发现y有时候返回这个界面时候程序会崩溃并报错没有初始化bmob
         * 故这里再次初始化一次
         */
        Bmob.initialize(this, "dcc6094bb058232c23ecd85b02f9ef4b");
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器

        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            try
            {
                BmobIM.init(this);
                BmobIM.registerDefaultMessageHandler(new MessageHandler());
            }
            catch (Exception e)
            {
                Log.d("hhh","bmobim init初始化失败");
                e.printStackTrace();
            }


            //TODO 用户管理：2.8、批量更新本地用户信息
           // BmobIM.getInstance().updateBatchUserInfo(List<BmobIMUserInfo>list);

        }


        //获取上个界面传过来的user信息
        try {
            user = (Users) this.getIntent().getSerializableExtra("user");
            phone = user.getPhone();
            connect(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initConfiguration();

        getRight();

        initView();

        initData();

        setInfo();
    }

    private void connect(Users user) {
       // final Users user = BmobUser.getCurrentUser(Users.class);
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                       /* BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()));*/
                       Log.d("hhh","connection zhong 连接成功");
                        //EventBus.getDefault().post(new RefreshEvent());
                    } else {
                        Toast.makeText(HomeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
            //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Toast.makeText(HomeActivity.this,status.getMsg(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }



    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化一些配置信息
     */
    private void initConfiguration() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

        //初始化progressdialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("信息修改中...");
        progressDialog.setCancelable(true);
    }


    /**
     * 检查缺少哪些定位权限并请求权限
     */
    private void getRight() {
        for (int i = 0; i < needPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(HomeActivity.this,
                    needPermissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(needPermissions[i]);
            }
        }
        //如果缺少权限那么去申请
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(HomeActivity.this, permissions, REQUEST_RIGHT);
        }
    }


    /**
     * 加载头像,用户名等信息
     */
    private void setInfo() {
        final String userIconUrl = user.getUserIcon();
        if (userIconUrl != null) {
            ImageLoader.getInstance().loadImage(userIconUrl, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    userIcon.setImageResource(R.mipmap.home_background);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    userIcon.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            });
        }

        //如果头像上传为空时候进行设置一张图片
        else {
            userIcon.setImageResource(R.mipmap.home_background);
        }

        //设置用户名
        userName.setText(user.getName());
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mNavigationMenu = (NavigationMenu) findViewById(R.id.navigationmenu);
        dataTv = (TextView) findViewById(R.id.data);
        weatherTv = (TextView) findViewById(R.id.weatherTV);
        baseTitle = (TextView) findViewById(R.id.title);
        monthTv = (TextView) findViewById(R.id.month);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        userIcon = (CircleImageView) headerView.findViewById(R.id.user_icon);
        userName = (TextView) headerView.findViewById(R.id.user_name);
        viewPager=(ViewPager)findViewById(R.id.recommend_content);

        baseTitle.setVisibility(View.GONE);
        //显示日期
        displayData();

        //设置viewpager
        fragmentList.add(RecommendContentFragment.newInstance(1,false));
        for (int i=2;i<14;i++){
            fragmentList.add(RecommendContentFragment.newInstance(i,true));
        }
        FragmentManager supportFragmentManager=getSupportFragmentManager();
        HomeFragmentPagerAdapter homeFragmentPagerAdapter=new HomeFragmentPagerAdapter(
                supportFragmentManager,fragmentList);
        viewPager.setAdapter(homeFragmentPagerAdapter);
        //viewpager最多有13个界面
        viewPager.setOffscreenPageLimit(13);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.found_icon);
        }


        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIcon.showContextMenu();
            }
        });

        userIcon.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "拍照");
                menu.add(1, 1, 1, "从相册中选");
            }
        });


        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.sign_out:
                        clearUserInfo();
                        break;
                    case R.id.cancel_push:
                       if (isReceivePush){
                           isReceivePush=false;
                           Toast.makeText(HomeActivity.this, "屏蔽推送成功",
                                   Toast.LENGTH_SHORT).show();
                       }
                       else {
                         openPush();
                           isReceivePush=true;
                          /* Toast.makeText(HomeActivity.this, "接收推送",
                                   Toast.LENGTH_SHORT).show();*/
                       }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        //悬浮框的点击事件
        mNavigationMenu.setOnMenuItemClickListener(new NavigationMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                if (view.getTag().equals("myRecords")) {
                    Intent intent = new Intent(HomeActivity.this, MyIssueRecordActivity.class);
                    intent.putExtra("user", user);
                    //Intent intent=new Intent(HomeActivity.this,ConversationActivity.class);
                    startActivity(intent);
                } else if (view.getTag().equals("lost")) {
                    Intent intent = new Intent(HomeActivity.this, LostGoodsActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else if (view.getTag().equals("found")) {
                    Intent intent = new Intent(HomeActivity.this, FoundGoodsActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else if (view.getTag().equals("search")) {
                    Intent intent = new Intent(HomeActivity.this, SearchInfoActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    private void openPush() {
        // 使用推送服务时的初始化操作
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    Log.d("hhh","openPush e==null");
                   // Log.d(bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Log.d("hhh","openPush e==null");
                   // Logger.e(e.getMessage());
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recommend_image:
                //displayPicture.showContextMenu();
                break;
        }

    }


    /**
     * 头像的context点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getGroupId()) {
            case 0:
                //选择拍照
                pickImageFromCamera();
                break;
            case 1:
                //选择相册
                pickImageFromAlbum();
                break;
            default:
                break;
        }
        return true;
    }


    //拍照
    private void pickImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFile = new File(file, System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, RESULT_FROM_CAMERA);
        } else {
            Toast.makeText(this, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    //从相册获取图片

    private void pickImageFromAlbum() {
        Intent picIntent = new Intent(Intent.ACTION_PICK, null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(picIntent, RESULT_FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_FROM_CAMERA:
                    startPhotoZoom(Uri.fromFile(mFile));
                    break;
                case RESULT_FROM_ALBUM:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    try {
                        Uri originalUri = data.getData();        //获得图片的uri
                        //这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = managedQuery(originalUri, proj, null,
                                null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        imagePath = cursor.getString(column_index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startPhotoZoom(data.getData());
                    break;
                case CUT_PHOTO:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
        } else {

        }
    }

    /**
     * 打开系统图片裁剪功能
     *
     * @param uri uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true); //黑边
        intent.putExtra("scaleUpIfNeeded", true); //黑边
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CUT_PHOTO);
    }


    /**
     * 显示修剪后的图片并进行上传
     * @param data
     */
    private void setPicToView(Intent data) {
        progressDialog.show();
        Bundle bundle = data.getExtras();
        if (bundle != null) {

            mBitmap = bundle.getParcelable("data");

            if (mFile != null) {
                imagePath = mFile.getPath();
            }

            //上传图片
            final BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //得到上传的图片地址
                        String fileUrl = bmobFile.getFileUrl();
                        user.setUserIcon(fileUrl);
                        //更新图片地址
                        user.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    userIcon.setImageBitmap(mBitmap);
                                    progressDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "修改信息成功",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(HomeActivity.this, "修改信息失败",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });

        }
    }


    /**
     * 清除用户信息，退出登录
     */
    private void clearUserInfo() {
        //清除SharedPreferences中的用户登录数据
        SharedPreferences sp = HomeActivity.this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        //跳转到登录界面
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 初始化定位数据
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
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        city = aMapLocation.getCity();
        //如果定位成功后就不用再定位，否则，没三秒定位一次直至定位成功
        if (!city.isEmpty()) {
            mlocationClient.stopLocation();
        }
        searchForcastWeather();

    }


    /**
     * 预报天气查询
     */
    private void searchForcastWeather() {
        //检索参数为城市和天气类型，实时天气为1、天气预报为2
        mquery = new WeatherSearchQuery(city, 1);
        mweathersearch = new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }


    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                weatherNow = city + "·" + weatherlive.getWeather() + " " + weatherlive.getTemperature() + "℃";
            } else {
                weatherNow = "地球·对流层  -273℃";
            }
        } else {
            weatherNow = "地球·对流层  -273℃";
        }
        weatherTv.setText(weatherNow);
    }


    /**
     * 天气预报，这里没有必须用到
     *
     * @param localWeatherForecastResult
     * @param i
     */
    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }


    /**
     * 设置日期
     */
    private void displayData() {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH) + 1;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String monthSimplify = null;
        switch (month) {
            case 1:
                monthSimplify = "Jan";
                break;
            case 2:
                monthSimplify = "Feb";
                break;
            case 3:
                monthSimplify = "Mar";
                break;
            case 4:
                monthSimplify = "Apr";
                break;
            case 5:
                monthSimplify = "May";
                break;
            case 6:
                monthSimplify = "June";
                break;
            case 7:
                monthSimplify = "July";
                break;
            case 8:
                monthSimplify = "Aug";
                break;
            case 9:
                monthSimplify = "Sep";
                break;
            case 10:
                monthSimplify = "Oct";
                break;
            case 11:
                monthSimplify = "Nov";
                break;
            case 12:
                monthSimplify = "Dec";
                break;
            default:
                break;
        }
        dataTv.setText(String.valueOf(day));
        monthTv.setText(monthSimplify + "·" + String.valueOf(year));
    }



    /**
     * 这里重载一下主要是实现两秒之内点击两次back键
     * 则退出程序
     */
    @Override
    public void onBackPressed() {
        if (isPressedBackOnce) {
            // 说明已经按了一次 这是第二次
            secondPressedTime = System.currentTimeMillis();
            if (secondPressedTime - firstPressedTime > 2000) {
                // 第一次点击作废了，重新计算
                Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
                isPressedBackOnce = true;
                firstPressedTime = System.currentTimeMillis();
            } else {
                // 说明两秒之内点击的第二次
                finish();
                isPressedBackOnce = false;
                firstPressedTime = 0;
                secondPressedTime = 0;
            }
        } else {
            // 说明第一次
            Toast.makeText(this, "再点一次退出",Toast.LENGTH_SHORT).show();
            isPressedBackOnce = true;
            firstPressedTime = System.currentTimeMillis();
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
