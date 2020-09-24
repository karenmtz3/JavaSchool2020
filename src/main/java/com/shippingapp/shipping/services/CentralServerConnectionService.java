package com.shippingapp.shipping.services;

public interface CentralServerConnectionService {
    String sendRequestAndReceiveResponse(String message);
}
