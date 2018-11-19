package com.example.elijah.skyranch_draft;

public class OrderItem{

    private long cart_id;
    private Product product;
    private long qty;
    private double amount;

    public OrderItem() {
    }

    public long getId() {
        return cart_id;
    }

    public void setId(long id) {
        this.cart_id = id;
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

    public double getAmount() {
        return this.amount;
    }


    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "cart_id=" + cart_id +
                ", product=" + product +
                ", qty=" + qty +
                ", amount=" + amount +
                "} \n";
    }
}
