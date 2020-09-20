package com.shippingapp.shipping.exception;

public class ResponseIsNullOrEmptyException extends ShippingAppException {
    private static final String message = "Response is empty or null";

    public ResponseIsNullOrEmptyException() {
        super(message);
    }
}
