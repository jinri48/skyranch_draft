package com.example.elijah.skyranch_draft;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private long id;
    private String name;
    private String imgUrl;
    private double o_price;

    private long arnoc;
    private String part_no;
    private String group_no;
    private String category;
    private char status;

    public Product() {
    }

    public Product(long id, String name, String imgUrl, double o_price) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.o_price = o_price;
    }

    public Product(long id, String name, String imgUrl, double o_price,
                   long arnoc, String part_no, String group_no, String category, char status) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.o_price = o_price;
        this.arnoc = arnoc;
        this.part_no = part_no;
        this.group_no = group_no;
        this.category = category;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public double getO_price() {
        return o_price;
    }

    public void setO_price(double o_price) {
        this.o_price = o_price;
    }

    public long getArnoc() {
        return arnoc;
    }

    public void setArnoc(long arnoc) {
        this.arnoc = arnoc;
    }

    public String getPart_no() {
        return part_no;
    }

    public void setPart_no(String part_no) {
        this.part_no = part_no;
    }

    public String getGroup_no() {
        return group_no;
    }

    public void setGroup_no(String group_no) {
        this.group_no = group_no;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", o_price=" + o_price +
                ", arnoc=" + arnoc +
                ", part_no=" + part_no +
                ", group_no=" + group_no +
                ", category=" + category +
                ", status=" + status +
                '}' + '\n';
    }

    // ---  parcel ---


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imgUrl);
        dest.writeDouble(this.o_price);
        dest.writeLong(this.arnoc);
        dest.writeString(this.part_no);
        dest.writeString(this.group_no);
        dest.writeString(this.category);
        dest.writeInt(this.status);
    }

    protected Product(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.imgUrl = in.readString();
        this.o_price = in.readDouble();
        this.arnoc = in.readLong();
        this.part_no = in.readString();
        this.group_no = in.readString();
        this.category = in.readString();
        this.status = (char) in.readInt();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
