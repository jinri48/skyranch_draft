package com.example.elijah.skyranch_draft;

public class OrderHeader {

    private long or_no;
    private String branch_id;
    private double net_amount;
    private double total_amount;
    private String trans_type;
    private String os_date;
    private Customer customer;
    private String cce_number;
    private String cce_name;

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
}
