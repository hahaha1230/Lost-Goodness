package com.example.lostgoodliness.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.lostgoodliness.javabean.UserInfo;

/**
 *
 * 这个类用于保存用户的登录信息
 * Created by 佳佳 on 10/18/2018.
 */

public class UserManage {
    private  static  UserManage instance;

    private UserManage(){

    }

    public static UserManage getInstance(){
        if (instance==null){
            instance=new UserManage();
        }
        return instance;
    }

    public  void  saveUserInfo(Context context,String userName,String password,boolean autoLogin){
        //Context.MODE_PRIVATE表示只有自己的程序才能调用，防止密码泄露
        SharedPreferences sp=context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("USER_NAME",userName);
        editor.putString("PASSWORD",password);
        editor.putBoolean("AUTO_LOGIN",autoLogin);
        editor.commit();
    }


    /**
     * 获取用户信息model
     *
     * @param context
     * @param
     * @param
     */
    public UserInfo getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(sp.getString("USER_NAME", ""));
        userInfo.setPassword(sp.getString("PASSWORD", ""));
        userInfo.setAutoLogin(sp.getBoolean("AUTO_LOGIN",false));
        return userInfo;
    }

    /**
     * userInfo中是否有数据
     */
    public boolean hasUserInfo(Context context) {
        UserInfo userInfo = getUserInfo(context);
        if (userInfo != null) {
            if ((!TextUtils.isEmpty(userInfo.getUserName())) && (!TextUtils.isEmpty(userInfo.getPassword()))) {//有数据
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 清除userInfo中的数据
     */
    private void clearUserInfo(Context context){
        SharedPreferences sp=context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
    }


}
