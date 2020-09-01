package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.shippingapp.shipping.config.Connection;
import com.shippingapp.shipping.exception.TransportServiceException;
import com.shippingapp.shipping.models.TransportType;
import com.shippingapp.shipping.services.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TransportServiceImpl implements TransportService {
    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final Connection connection;

    private List<TransportType> transportTypeList;

    private final ObjectMapper objectMapper;

    @Autowired
    public TransportServiceImpl(AmqpTemplate rabbitTemplate, Connection connection) {
        this.rabbitTemplate = rabbitTemplate;
        this.connection = connection;
        transportTypeList = new ArrayList<>();
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionForTransportTypes() {
        String message = "{\"type\":\"transportType\"}";
        Object messageObject = rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), message);
        String response = objectMapper.convertValue(messageObject, new TypeReference<String>() {
        });

        logger.info("response transport type {}", response);
        return getDescriptionTypesList(response);
    }

    private List<String> getDescriptionTypesList(String response) {
        List<String> descriptionList = new ArrayList<>();
        if (response == null || response.trim().isEmpty()) {
            logger.error("response of transport type is null or empty");
            throw new TransportServiceException("Error to get package types");
        }
        List<TransportType> transportTypes = new Gson().fromJson(response,
                new TypeReference<List<TransportType>>() {
                }.getType());
        transportTypes.removeIf(tt -> tt.getId() == 0 || tt.getDescription().isEmpty());
        transportTypeList = transportTypes.stream().filter(distinctByKey(TransportType::getId))
                .collect(Collectors.toList());
        for (TransportType pt : transportTypeList) {
            descriptionList.add(pt.getDescription());
        }
        return descriptionList;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
