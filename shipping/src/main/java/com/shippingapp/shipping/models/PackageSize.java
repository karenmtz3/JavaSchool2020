package com.shippingapp.shipping.models;

public class PackageSize {
    private int id;
    private String description;
    private int priceFactor;

    public PackageSize(int id, String description, int priceFactor) {
        this.id = id;
        this.description = description;
        this.priceFactor = priceFactor;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getPriceFactor() {
        return priceFactor;
    }
}
