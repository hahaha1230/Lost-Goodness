package com.example.lostgoodliness.javabean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 佳佳 on 10/18/2018.
 */

public class Picture extends BmobObject{
    private int PictureIndex;
    private BmobFile picture;
    private String pictureAuthor;
    private String satement;
    private String satementAuthor;

    public int getPictureIndex() {
        return PictureIndex;
    }

    public void setPictureIndex(int pictureIndex) {
        PictureIndex = pictureIndex;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public String getPictureAuthor() {
        return pictureAuthor;
    }

    public void setPictureAuthor(String pictureAuthor) {
        this.pictureAuthor = pictureAuthor;
    }

    public String getSatement() {
        return satement;
    }

    public void setSatement(String satement) {
        this.satement = satement;
    }

    public String getSatementAuthor() {
        return satementAuthor;
    }

    public void setSatementAuthor(String satementAuthor) {
        this.satementAuthor = satementAuthor;
    }
}
