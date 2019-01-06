package com.example.elijah.skyranch_draft;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class LoginToken implements Parcelable {

    private String id;
    private String token;
    private String name;
    private long branch;


    public LoginToken(String id, String token, String name, long branch) {
        this.id = id;
        this.token = token;
        this.name = name;
        this.branch = branch;
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


    public long getBranch() {
        return branch;
    }

    public void setBranch(long branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "LoginToken{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", branch=" + branch +
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
        dest.writeLong(this.branch);
    }

    protected LoginToken(Parcel in) {
        this.id = in.readString();
        this.token = in.readString();
        this.name = in.readString();
        this.branch = in.readLong();
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
