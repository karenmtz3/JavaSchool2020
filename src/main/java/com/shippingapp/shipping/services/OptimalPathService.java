package com.shippingapp.shipping.services;

import com.shippingapp.shipping.models.CityPath;

import java.util.List;

public interface OptimalPathService {
    String getOptimalPathBetweenTwoCities(List<CityPath> cityPaths, String origin, String destination);
}
