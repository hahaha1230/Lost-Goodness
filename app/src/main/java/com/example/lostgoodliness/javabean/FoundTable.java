package com.example.lostgoodliness.javabean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by 佳佳 on 10/10/2018.
 */

public class FoundTable extends BmobObject implements Serializable{
    private String findTime;         //捡到时间
    private String findWhere;        //捡到地点
    private String findType;         //类型
    private String goodDescribe;    //物品描述
    private String phone;            //手机号
    private String city;            //城市
    private String foundGoodImage; //捡到物品图片
    private double latitude;       //捡到物品经度
    private double longitude;      //捡到物品纬度
    private Users linkUsers;       //链接表（链接Users表）

    public Users getLinkUsers() {
        return linkUsers;
    }

    public void setLinkUsers(Users linkUsers) {
        this.linkUsers = linkUsers;
    }

    public String getFoundGoodImage() {
        return foundGoodImage;
    }

    public void setFoundGoodImage(String foundGoodImage) {
        this.foundGoodImage = foundGoodImage;
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


    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getFindWhere() {
        return findWhere;
    }

    public void setFindWhere(String findWhere) {
        this.findWhere = findWhere;
    }

    public String getFindType() {
        return findType;
    }

    public void setFindType(String findType) {
        this.findType = findType;
    }

    public String getGoodDescribe() {
        return goodDescribe;
    }

    public void setGoodDescribe(String goodDescribe) {
        this.goodDescribe = goodDescribe;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
