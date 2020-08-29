package com.shippingapp.shipping.models;

public class PackageType {
    private int id;
    private String description;
    private int price;

    public PackageType(int id, String description, int price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}
