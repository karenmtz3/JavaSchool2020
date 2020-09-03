package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.*;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.PackageServiceException;
import com.shippingapp.shipping.exception.PackageTypeIsNullOrEmptyException;
import com.shippingapp.shipping.models.PackageSize;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PackageServiceImpl implements PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
    }

    public List<String> getDescriptionsForPackageTypes() {
        String message = "{\"type\":\"packageType\"}";
        Object messageResponse;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (Exception ex) {
            throw new CentralServerException("Central server can't get response");
        }

        logger.info("response package type {}", messageResponse);
        if (messageResponse == null || messageResponse.toString().isEmpty()) {
            logger.error("response of package type is empty");
            throw new PackageServiceException("response of package type is empty");
        }
        List<PackageType> packageTypes = new Gson().fromJson(messageResponse.toString(),
                new TypeReference<List<PackageType>>() {
                }.getType());
        return getDescriptionTypesList(packageTypes);
    }

    private List<String> getDescriptionTypesList(List<PackageType> packageTypesList) {
        List<PackageType> packageTypesListFiltered = packageTypesList
                .stream()
                .filter(pt -> pt.getId() != 0 && !pt.getDescription().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        return packageTypesListFiltered
                .stream()
                .map(PackageType::getDescription)
                .collect(Collectors.toList());
    }

    public List<String> getDescriptionsForPackageSize(String packageType) {
        if (packageType == null || packageType.trim().isEmpty()) {
            logger.error("packageType can't be empty or null");
            throw new PackageTypeIsNullOrEmptyException("Error to get package sizes");
        }
        String message = "{\"type\":\"packageSizeByType\",\"packageType\":\"" + packageType + "\"}";
        Object messageResponse;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (Exception ex) {
            throw new CentralServerException("Central server can't get response");
        }

        logger.info("response package size {}", messageResponse);
        if (messageResponse == null || messageResponse.toString().isEmpty()) {
            logger.error("response of package size is empty");
            throw new PackageServiceException("response of package size is empty");
        }
        List<PackageSize> packageSizes = new Gson().fromJson(messageResponse.toString(),
                new TypeReference<List<PackageSize>>() {
                }.getType());
        return getDescriptionSizesList(packageSizes);
    }

    private List<String> getDescriptionSizesList(List<PackageSize> packageSizes) {
        List<PackageSize> packageSizesListFiltered = packageSizes
                .stream()
                .filter(ps -> ps.getId() != 0 && !ps.getDescription().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        return packageSizesListFiltered
                .stream()
                .map(PackageSize::getDescription)
                .collect(Collectors.toList());
    }
}
