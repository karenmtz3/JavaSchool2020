package com.shippingapp.shipping.models;

public class TransportType {
    private int id;
    private String description;
    private int pricePerMile;

    public TransportType(int id, String description, int pricePerMile) {
        this.id = id;
        this.description = description;
        this.pricePerMile = pricePerMile;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getPricePerMile() {
        return pricePerMile;
    }
}
