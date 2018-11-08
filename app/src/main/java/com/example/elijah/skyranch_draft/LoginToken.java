package com.example.elijah.skyranch_draft;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class LoginToken implements Parcelable {
    private static long num = 0;
    private String id;
    private String token;
    private String name;



    public LoginToken(String id, String token, String name) {
        this.id = id;
        this.token = token;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return "LoginToken{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.token);
        dest.writeString(this.name);
    }

    protected LoginToken(Parcel in) {
        this.id = in.readString();
        this.token = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<LoginToken> CREATOR = new Parcelable.Creator<LoginToken>() {
        @Override
        public LoginToken createFromParcel(Parcel source) {
            return new LoginToken(source);
        }

        @Override
        public LoginToken[] newArray(int size) {
            return new LoginToken[size];
        }
    };



}
