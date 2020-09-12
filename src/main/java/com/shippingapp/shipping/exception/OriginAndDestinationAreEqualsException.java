package com.shippingapp.shipping.exception;

public class OriginAndDestinationAreEqualsException extends CityServiceException{
    public OriginAndDestinationAreEqualsException(String message) {
        super(message);
    }
}
