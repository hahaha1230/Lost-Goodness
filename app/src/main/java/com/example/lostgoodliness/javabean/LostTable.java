package com.example.lostgoodliness.javabean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 佳佳 on 10/10/2018.
 */

public class LostTable extends BmobObject {
    private String phone;
    private String userName;
    private String lostTime;
    private String lostType;
    private String lostWhere;
    private String goodsDescribe;
    private String city;
    private String lostGoodImage;
    private double latitude;
    private double longitude;


    public String getLostGoodImage() {
        return lostGoodImage;
    }

    public void setLostGoodImage(String lostGoodsImage) {
        this.lostGoodImage = lostGoodsImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLostTime() {
        return lostTime;
    }

    public void setLostTime(String lostTime) {
        this.lostTime = lostTime;
    }

    public String getLostType() {
        return lostType;
    }

    public void setLostType(String lostType) {
        this.lostType = lostType;
    }

    public String getLostWhere() {
        return lostWhere;
    }

    public void setLostWhere(String lostWhere) {
        this.lostWhere = lostWhere;
    }

    public String getGoodsDescribe() {
        return goodsDescribe;
    }

    public void setGoodsDescribe(String goodsDescribe) {
        this.goodsDescribe = goodsDescribe;
    }
}
