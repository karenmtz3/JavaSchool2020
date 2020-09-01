package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.PackageServiceException;
import com.shippingapp.shipping.models.PackageSize;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PackageServiceImpl implements PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    private final ObjectMapper objectMapper;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionsForPackageTypes() {
        String message = "{\"type\":\"packageType\"}";

        Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), message);
        String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
        });

        logger.info("response package type {}", response);
        if (response == null || response.trim().isEmpty()) {
            logger.error("response of package type is null or empty");
            throw new PackageServiceException("response of package type is null or empty",
                    HttpStatus.BAD_GATEWAY);
        } else {
            return getDescriptionTypesList(response);
        }
    }

    private List<String> getDescriptionTypesList(String response) {
        List<String> descriptionList = new ArrayList<>();
        List<PackageType> packageTypeList = getPackageTypesList(response);

        for (PackageType pt : packageTypeList) {
            descriptionList.add(pt.getDescription());
        }
        return descriptionList;
    }

    private List<PackageType> getPackageTypesList(String response) {
        List<PackageType> packageTypes = new Gson().fromJson(response,
                new TypeReference<List<PackageType>>() {
                }.getType());
        packageTypes.removeIf(pt -> pt.getId() == 0 || pt.getDescription().isEmpty());
        Set<PackageType> PackageTypesSet = new LinkedHashSet<>(packageTypes);
        packageTypes.clear();
        packageTypes.addAll(PackageTypesSet);

        return packageTypes;
    }

    public List<String> getDescriptionsForPackageSize(String packageType) {
        if (packageType == null || packageType.trim().isEmpty()) {
            logger.error("packageType can't be empty or null");
            throw new PackageServiceException("Error to get package sizes", HttpStatus.NOT_ACCEPTABLE);
        }
        String message = "{\"type\":\"packageSizeByType\",\"packageType\":\"" + packageType + "\"}";
        Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), message);
        String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
        });

        logger.info("response package size {}", response);
        if (response == null || response.trim().isEmpty()) {
            logger.error("response of package size is null or empty");
            throw new PackageServiceException("response of package size is null or empty",
                    HttpStatus.BAD_GATEWAY);
        } else {
            return getDescriptionSizesList(response);
        }
    }

    private List<String> getDescriptionSizesList(String response) {
        List<String> descriptionList = new ArrayList<>();

        List<PackageSize> packageSizeList = getPackageSizesList(response);

        for (PackageSize ps : packageSizeList) {
            descriptionList.add(ps.getDescription());
        }
        return descriptionList;
    }

    private List<PackageSize> getPackageSizesList(String response) {
        List<PackageSize> packageSizes = new Gson().fromJson(response,
                new TypeReference<List<PackageSize>>() {
                }.getType());
        packageSizes.removeIf(ps -> ps.getId() == 0 || ps.getDescription().isEmpty());
        Set<PackageSize> PackageSizesSet = new LinkedHashSet<>(packageSizes);
        packageSizes.clear();
        packageSizes.addAll(PackageSizesSet);

        return packageSizes;
    }
}
