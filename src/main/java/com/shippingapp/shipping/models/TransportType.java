package com.shippingapp.shipping.models;

import java.io.Serializable;
import java.util.Objects;

public final class TransportType implements Serializable {
    private final int id;
    private final String description;
    private final int pricePerMile;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransportType that = (TransportType) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
