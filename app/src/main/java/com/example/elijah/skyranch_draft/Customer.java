package com.example.elijah.skyranch_draft;

import android.os.Parcel;
import android.os.Parcelable;

import ir.mirrajabi.searchdialog.core.Searchable;

public class Customer implements Parcelable {

    private long id;
    private String fname;
    private String lname;
    private String mobile;
    private String email;
    private String bday;
    private String name;
    private boolean isLoyalty;

    public Customer() {
    }

    public Customer(long id, String fname, String lname, String mobile, String email, String bday) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.mobile = mobile;
        this.email = email;
        this.bday = bday;
        this.name = fname + " " +lname;
    }

    public Customer(long id, String mobile, String email, String bday, String name) {
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.bday = bday;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoyalty() {
        return isLoyalty;
    }

    public void setLoyalty(boolean loyalty) {
        isLoyalty = loyalty;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", bday='" + bday + '\'' +
                ", name='" + name + '\'' +
                ", isLoyalty=" + isLoyalty +
                "} \n";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
        dest.writeString(this.mobile);
        dest.writeString(this.email);
        dest.writeString(this.bday);
        dest.writeString(this.name);
        dest.writeByte(this.isLoyalty ? (byte) 1 : (byte) 0);
    }

    protected Customer(Parcel in) {
        this.id = in.readLong();
        this.fname = in.readString();
        this.lname = in.readString();
        this.mobile = in.readString();
        this.email = in.readString();
        this.bday = in.readString();
        this.name = in.readString();
        this.isLoyalty = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}