package com.example.elijah.skyranch_draft;

import java.util.List;

public class ShoppingCart {

    private List<OrderItem> items;
    private double total;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotal(){
        for (OrderItem tempItems: items) {
            this.total += tempItems.getAmount();
        }
        return this.total;
    }
}
