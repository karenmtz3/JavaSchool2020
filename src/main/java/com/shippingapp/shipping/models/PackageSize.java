package com.shippingapp.shipping.models;

import java.io.Serializable;
import java.util.Objects;

public final class PackageSize implements Serializable {
    private final int id;
    private final String description;
    private final int priceFactor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageSize size = (PackageSize) o;
        return id == size.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}