package com.shippingapp.shipping.exception;

public class CentralServerException extends ShippingAppException {
    private static final String message = "Central server can't get response";

    public CentralServerException() {
        super(message);
    }
}
