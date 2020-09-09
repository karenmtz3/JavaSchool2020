package com.shippingapp.shipping.models;

public final class City {
    private final int id;
    private final String name;
    private final boolean seaport;
    private final boolean airport;

    public City(int id, String name, boolean seaport, boolean airport) {
        this.id = id;
        this.name = name;
        this.seaport = seaport;
        this.airport = airport;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSeaport() {
        return seaport;
    }

    public boolean isAirport() {
        return airport;
    }
}
