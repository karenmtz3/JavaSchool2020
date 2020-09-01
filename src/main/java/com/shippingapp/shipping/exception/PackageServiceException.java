package com.shippingapp.shipping.exception;

import org.springframework.http.HttpStatus;

public class PackageServiceException extends RuntimeException {
    private final HttpStatus status;

    public PackageServiceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
