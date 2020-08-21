package com.shippingapp.shipping.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PackageService {
    List<String> getPackagesType();

}
