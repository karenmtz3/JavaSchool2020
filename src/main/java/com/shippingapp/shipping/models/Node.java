package com.shippingapp.shipping.models;

public class Node {
    private final String city;
    private final Integer cost;

    public Node(String city, Integer cost) {
        this.city = city;
        this.cost = cost;
    }

    public String getCity() {
        return city;
    }

    public Integer getCost() {
        return cost;
    }
}