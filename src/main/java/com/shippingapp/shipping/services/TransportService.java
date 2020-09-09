package com.shippingapp.shipping.services;

import java.util.List;

public interface TransportService {
    List<String> getDescriptionForTransportTypes();

    List<String> getDescriptionForTransportVelocity();
}