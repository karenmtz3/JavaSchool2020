package com.shippingapp.shipping.dto;

public class RequestType {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RequestType{" + "type='" + type + '\'' + '}';
    }
}
