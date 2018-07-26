package com.example.joon.kcec.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
    private String caption;
    private String photo_id;
    private String image_path;

    public Photo() {
    }

    public Photo(String image_path) {
//        this.caption = caption;
//        this.photo_id = photo_id;
        this.image_path = image_path;
    }

    protected Photo(Parcel in) {
        caption = in.readString();
        photo_id = in.readString();
        image_path = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

//    public String getCaption() {
//        return caption;
//    }
//
//    public void setCaption(String caption) {
//        this.caption = caption;
//    }
//
//    public String getPhoto_id() {
//        return photo_id;
//    }
//
//    public void setPhoto_id(String photo_id) {
//        this.photo_id = photo_id;
//    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(photo_id);
        dest.writeString(image_path);
    }
}
