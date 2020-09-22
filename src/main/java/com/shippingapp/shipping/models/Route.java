package com.shippingapp.shipping.models;

import java.util.List;

public class Route {
    private final List<String> path;
    private final int cost;

    public Route(List<String> path, int cost) {
        this.path = path;
        this.cost = cost;
    }

    public List<String> getPath() {
        return path;
    }

    public int getCost() {
        return cost;
    }
}
