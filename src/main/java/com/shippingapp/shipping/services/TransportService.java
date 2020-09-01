package com.shippingapp.shipping.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransportService {
    List<String> getDescriptionForTransportTypes();
}