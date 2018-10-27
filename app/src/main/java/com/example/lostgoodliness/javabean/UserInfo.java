package com.example.lostgoodliness.javabean;

/**
 *
 * 这里的是用于从本地数据库获得账户密码的javabean
 * Created by 佳佳 on 10/18/2018.
 */

public class UserInfo {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     *是否自动登录
     */
    private boolean autoLogin;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }
}
