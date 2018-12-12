package com.example.elijah.skyranch_draft;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class OrderHeader implements Parcelable {

    private long or_no;
    private String branch_id;
    private double net_amount;
    private double total_amount;
    private String trans_type;
    private String os_date;
    private Customer customer;
    private String cce_number;
    private String cce_name;
    private String status;

    public OrderHeader() {
    }

    public OrderHeader(long or_no, String branch_id, double net_amount,
                       double total_amount, String trans_type, String os_date,
                       Customer customer, String cce_number, String cce_name) {

        this.or_no = or_no;
        this.branch_id = branch_id;
        this.net_amount = net_amount;
        this.total_amount = total_amount;
        this.trans_type = trans_type;
        this.os_date = os_date;
        this.customer = customer;
        this.cce_number = cce_number;
        this.cce_name = cce_name;
    }

    public long getOr_no() {
        return or_no;
    }

    public void setOr_no(long or_no) {
        this.or_no = or_no;
    }

    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(String branch_id) {
        this.branch_id = branch_id;
    }

    public double getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(double net_amount) {
        this.net_amount = net_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public String getOs_date() {
        return os_date;
    }

    public void setOs_date(String os_date) {
        this.os_date = os_date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCce_number() {
        return cce_number;
    }

    public void setCce_number(String cce_number) {
        this.cce_number = cce_number;
    }

    public String getCce_name() {
        return cce_name;
    }

    public void setCce_name(String cce_name) {
        this.cce_name = cce_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderHeader{" +
                "or_no=" + or_no +
                ", branch_id='" + branch_id + '\'' +
                ", net_amount=" + net_amount +
                ", total_amount=" + total_amount +
                ", trans_type=" + trans_type +
                ", os_date='" + os_date + '\'' +
                ", customer=" + customer +
                ", cce_number=" + cce_number +
                ", cce_name='" + cce_name + '\'' +
                "} \n";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.or_no);
        dest.writeString(this.branch_id);
        dest.writeDouble(this.net_amount);
        dest.writeDouble(this.total_amount);
        dest.writeString(this.trans_type);
        dest.writeString(this.os_date);
        dest.writeParcelable(this.customer, flags);
        dest.writeString(this.cce_number);
        dest.writeString(this.cce_name);
        dest.writeString(this.status);
    }

    protected OrderHeader(Parcel in) {
        this.or_no = in.readLong();
        this.branch_id = in.readString();
        this.net_amount = in.readDouble();
        this.total_amount = in.readDouble();
        this.trans_type = in.readString();
        this.os_date = in.readString();
        this.customer = in.readParcelable(Customer.class.getClassLoader());
        this.cce_number = in.readString();
        this.cce_name = in.readString();
        this.status = in.readString();
    }

    public static final Parcelable.Creator<OrderHeader> CREATOR = new Parcelable.Creator<OrderHeader>() {
        @Override
        public OrderHeader createFromParcel(Parcel source) {
            return new OrderHeader(source);
        }

        @Override
        public OrderHeader[] newArray(int size) {
            return new OrderHeader[size];
        }
    };
}
