package com.shippingapp.shipping.services;

import com.shippingapp.shipping.dto.RequestType;
import org.springframework.stereotype.Service;

@Service
public interface WebService {
    void sendRequest(RequestType requestType);

}
