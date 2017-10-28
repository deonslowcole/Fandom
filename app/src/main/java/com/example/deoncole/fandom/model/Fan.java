package com.example.deoncole.fandom.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class Fan extends User implements Parcelable {

    public static final String FAN_NAME_COL = "fanName";
    private String fanName;
    @Nullable
    private String imageUrl;

    public Fan() {
    }

    public Fan(String userUid, String fanName, @Nullable String imageUrl) {
        super(userUid);
        this.fanName = fanName;
        this.imageUrl = imageUrl;
    }

    public String getFanName() {
        return fanName;
    }

    public void setFanName(String fanName) {
        this.fanName = fanName;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fanName);
        dest.writeString(this.imageUrl);
    }

    private Fan(Parcel in) {
        this.fanName = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Fan> CREATOR = new Parcelable.Creator<Fan>() {
        @Override
        public Fan createFromParcel(Parcel source) {
            return new Fan(source);
        }

        @Override
        public Fan[] newArray(int size) {
            return new Fan[size];
        }
    };
}
