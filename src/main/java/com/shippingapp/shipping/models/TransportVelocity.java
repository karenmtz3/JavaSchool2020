package com.shippingapp.shipping.models;

import java.util.Objects;

public final class TransportVelocity {
    private final int id;
    private final String description;
    private final int priceFactor;

    public TransportVelocity(int id, String description, int priceFactor) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportVelocity that = (TransportVelocity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
