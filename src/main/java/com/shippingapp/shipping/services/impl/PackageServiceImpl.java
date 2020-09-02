package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper objectMapper;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionsForPackageTypes() {
        String message = "{\"type\":\"packageType\"}";

        try {
            Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
            String response = objectMapper.convertValue(messageObject,
                    new TypeReference<String>() {
                    });

            logger.info("response package type {}", response);
            if (response == null || response.trim().isEmpty()) {
                logger.error("response of package type is null or empty");
                throw new PackageServiceException("response of package type is null or empty");
            }
            return getDescriptionTypesList(response);
        } catch (Exception ex) {
            throw new CentralServerException("Central server can't get response");
        }
    }

    private List<String> getDescriptionTypesList(String response) {
        return getPackageTypesList(response)
                .stream()
                .map(PackageType::getDescription)
                .collect(Collectors.toList());
    }

    private List<PackageType> getPackageTypesList(String response) {
        List<PackageType> packageTypes = new Gson().fromJson(response,
                new TypeReference<List<PackageType>>() {
                }.getType());

        return packageTypes
                .stream()
                .filter(pt -> pt.getId() != 0 && !pt.getDescription().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getDescriptionsForPackageSize(String packageType) {
        if (packageType == null || packageType.trim().isEmpty()) {
            logger.error("packageType can't be empty or null");
            throw new PackageTypeIsNullOrEmptyException("Error to get package sizes");
        }
        try {
            String message = "{\"type\":\"packageSizeByType\",\"packageType\":\"" + packageType + "\"}";
            Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
            String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
            });

            logger.info("response package size {}", response);
            if (response == null || response.trim().isEmpty()) {
                logger.error("response of package size is null or empty");
                throw new PackageServiceException("response of package size is null or empty");
            }
            return getDescriptionSizesList(response);
        } catch (Exception ex) {
            throw new CentralServerException("Central server can't get response");
        }
    }

    private List<String> getDescriptionSizesList(String response) {

        return getPackageSizesList(response)
                .stream()
                .map(PackageSize::getDescription)
                .collect(Collectors.toList());

    }

    private List<PackageSize> getPackageSizesList(String response) {
        List<PackageSize> packageSizes = new Gson().fromJson(response,
                new TypeReference<List<PackageSize>>() {
                }.getType());

        return packageSizes
                .stream()
                .filter(ps -> ps.getId() != 0 && !ps.getDescription().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
