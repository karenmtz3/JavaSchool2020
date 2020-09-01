package com.shippingapp.shipping.services;

import java.util.List;

public interface PackageService {
    List<String> getDescriptionsForPackageTypes();

    List<String> getDescriptionsForPackageSize(String packageType);
}
