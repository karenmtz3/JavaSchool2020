package com.shippingapp.shipping.services;

import java.util.List;

public interface CityService {
    List<String> getCityNames();

    String getFirstPath(String origin, String destination);
}
