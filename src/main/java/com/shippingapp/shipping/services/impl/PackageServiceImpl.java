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
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class PackageServiceImpl implements PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    private List<PackageType> packageTypeList;
    private List<PackageSize> packageSizeList;

    private final ObjectMapper objectMapper;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
        packageTypeList = new ArrayList<>();
        packageSizeList = new ArrayList<>();
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionsForPackageTypes() {
        String message = "{\"type\":\"packageType\"}";

        Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), message);
        String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
        });

        logger.info("response package type {}", response);
        return getDescriptionTypesList(response);
    }

    private List<String> getDescriptionTypesList(String response) {
        List<String> descriptionList = new ArrayList<>();
        if (response == null || response.trim().isEmpty()) {
            logger.error("response of package type is null or empty");
            throw new PackageServiceException("Error to get package types");
        }
        List<PackageType> packageTypes = new Gson().fromJson(response,
                new TypeReference<List<PackageType>>() {
                }.getType());
        packageTypes.removeIf(pt -> pt.getId() == 0 || pt.getDescription().isEmpty());
        packageTypeList = packageTypes.stream().filter(distinctByKey(PackageType::getId))
                .collect(Collectors.toList());
        for (PackageType pt : packageTypeList) {
            descriptionList.add(pt.getDescription());
        }
        return descriptionList;
    }

    public List<String> getDescriptionsForPackageSize(String packageType) {
        if (packageType == null || packageType.trim().isEmpty()) {
            logger.error("packageType can't be empty or null");
            throw new PackageServiceException("Error to get package sizes");
        }
        String message = "{\"type\":\"packageSizeByType\",\"packageType\":\"" + packageType + "\"}";
        Object messageObject = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), message);
        String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
        });

        logger.info("response package type {}", response);
        return getDescriptionSizesList(response);
    }

    private List<String> getDescriptionSizesList(String response) {
        List<String> descriptionList = new ArrayList<>();
        if (response == null || response.trim().isEmpty()) {
            logger.error("response of package size is null or empty");
            throw new PackageServiceException("Error to get package sizes");
        }
        List<PackageSize> packageSizes = new Gson().fromJson(response,
                new TypeReference<List<PackageSize>>() {
                }.getType());
        packageSizes.removeIf(ps -> ps.getId() == 0 || ps.getDescription().isEmpty());
        packageSizeList = packageSizes.stream().filter(distinctByKey(PackageSize::getId))
                .collect(Collectors.toList());
        for (PackageSize ps : packageSizeList) {
            descriptionList.add(ps.getDescription());
        }
        return descriptionList;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
