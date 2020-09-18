package com.shippingapp.shipping.services;

import com.shippingapp.shipping.models.CityDTO;

import java.util.List;

public interface CityService {
    List<String> getCityNames();

    String getFirstPath(CityDTO cityDTO);
}
