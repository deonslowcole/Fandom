package com.example.deoncole.fandom.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist extends User implements Parcelable {

    public static final String ARTISTS_REF = "artists";
    public static final String ARTIST_NAME_COL = "artistName";

    private String id;
    private String artistName;
    private String imageUrl;

    public Artist() {
    }

    public Artist(String userUid, String artistName, String imageUrl) {
        super(userUid);
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public Artist(String id, String userUid, String artistName, String imageUrl) {
        super(userUid);
        this.id = id;
        this.artistName = artistName;
        this.imageUrl = imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.artistName);
        dest.writeString(this.imageUrl);
    }

    protected Artist(Parcel in) {
        this.id = in.readString();
        this.artistName = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public String toString() {
        return "Artist{" +
                "id='" + id + '\'' +
                ", artistName='" + artistName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
