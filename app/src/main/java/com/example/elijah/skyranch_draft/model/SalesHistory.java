package com.example.elijah.skyranch_draft.model;

import com.example.elijah.skyranch_draft.Customer;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.OrderItem;

import java.util.ArrayList;

public class SalesHistory {

    private double netAmount;
    private ArrayList<OrderHeader> orders;
    private String from;
    private String to;


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public ArrayList<OrderHeader> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<OrderHeader> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "SalesHistory{" +
                "netAmount=" + netAmount +
                ", orders=" + orders +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
