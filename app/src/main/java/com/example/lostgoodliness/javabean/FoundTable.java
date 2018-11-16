package com.example.lostgoodliness.javabean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by 佳佳 on 10/10/2018.
 */

public class FoundTable extends BmobObject implements Serializable{
    private String findTime;
    private String findWhere;
    private String findType;
    private String goodDescribe;
    private String phone;
    private String userName;
    private String city;
    private String foundGoodImage;
    private double latitude;
    private double longitude;
    private Users linkUsers;

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
