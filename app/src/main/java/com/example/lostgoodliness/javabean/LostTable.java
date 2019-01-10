package com.example.lostgoodliness.javabean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 佳佳 on 10/10/2018.
 */

public class LostTable extends BmobObject {
    private String phone;                  //记录电话
    private String userName;              //记录用户名
    private String lostTime;              //丢失时间
    private String lostType;              //丢失类型
    private String lostWhere;             //丢失地点
    private String goodsDescribe;         //物品描述
    private String city;                   //丢失城市
    private String lostGoodImage;         //丢失物品照片
    private double latitude;              //丢失地点经度
    private double longitude;             //丢失地点纬度
    private Users linkUsers;               //链接表（链接Users）


    public Users getLinkUsers() {
        return linkUsers;
    }

    public void setLinkUsers(Users linkUsers) {
        this.linkUsers = linkUsers;
    }

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
