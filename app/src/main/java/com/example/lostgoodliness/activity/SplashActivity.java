package com.example.lostgoodliness.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.lostgoodliness.R;
import com.example.lostgoodliness.javabean.Users;
import com.example.lostgoodliness.manage.UserManage;

import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends AppCompatActivity {
    private static final int GO_HOME=0;//去home界面
    private static final int GO_LOGIN=1;//去登录界面
    private Users user=new Users();
    private String phone=null;
    private String password=null;
    private boolean autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        initBmob();

        if (UserManage.getInstance().hasUserInfo(this)){
            autoLogin=UserManage.getInstance().getUserInfo(this).getAutoLogin();
            if (autoLogin){
                phone=UserManage.getInstance().getUserInfo(this).getUserName();
                getUser(phone);
                handler.sendEmptyMessageDelayed(GO_HOME,2000);
            }
            else {
                phone=UserManage.getInstance().getUserInfo(this).getUserName();
                password=UserManage.getInstance().getUserInfo(this).getPassword();
                handler.sendEmptyMessageAtTime(GO_LOGIN,2000);
            }
        }
        else{
            handler.sendEmptyMessageAtTime(GO_LOGIN,2000);
        }
    }

    /**
     * 初始化bmob
     */

    private void initBmob(){
        /**
         * 设置key
         */
        Bmob.initialize(this, "dcc6094bb058232c23ecd85b02f9ef4b");
        //初始化bmob的push功能

        //bmob消息推送
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    Log.d("hhh", bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                } else {
                    Log.d("hhh", e.getMessage());
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(this);
    }


    /**
     * 根据phone获取用户信息
     * @param phone
     */
    private void getUser(String phone){
        BmobQuery<Users> query = new BmobQuery<Users>();
        query.addWhereEqualTo("phone", phone);
        query.findObjects(new FindListener<Users>() {
            @Override
            public void done(List<Users> list, BmobException e) {
                if (e==null){
                    user=list.get(0);
                }
                else {//搜索出现异常

                }
            }
        });

    }


    /**
     * 跳转判断
     */
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case GO_HOME:
                   Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                   intent.putExtra("user",user);
                   startActivity(intent);
                   finish();
                   break;
               case GO_LOGIN:
                   Intent intent2=new Intent(SplashActivity.this,LoginActivity.class);
                   if (phone!=null);
                   {
                       intent2.putExtra("phone",phone);
                       intent2.putExtra("password",password);
                   }
                   startActivity(intent2);
                   finish();
           }
        }
    };
}
