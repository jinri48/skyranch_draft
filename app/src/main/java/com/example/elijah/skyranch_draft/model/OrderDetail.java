package com.example.elijah.skyranch_draft.model;

import com.example.elijah.skyranch_draft.Product;

public class OrderDetail {

    private long id;
    private Product product;
    private long qty;
    private double total_amount;
    private double net_amount;
    private double discount;
    private String status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public double getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(double net_amount) {
        this.net_amount = net_amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
