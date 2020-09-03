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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportServiceImpl implements TransportService {
    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final Connection connection;

    private final ObjectMapper objectMapper;

    @Autowired
    public TransportServiceImpl(AmqpTemplate rabbitTemplate, Connection connection) {
        this.rabbitTemplate = rabbitTemplate;
        this.connection = connection;
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionForTransportTypes() {
        try {
            String message = "{\"type\":\"transportType\"}";
            Object messageObject = rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                    connection.getRoutingKey(), message);
            String response = objectMapper.convertValue(messageObject,
                    new TypeReference<String>() {
                    });

            logger.info("response transport type {}", response);
            if (response == null || response.trim().isEmpty()) {
                logger.error("response of transport type is null or empty");
                throw new TransportServiceException("Error to get package types");
            }
            return getDescriptionTypesList(response);
        } catch (Exception e) {
            throw new TransportServiceException("Central server can't get response");
        }
    }

    private List<String> getDescriptionTypesList(String response) {
        return getTransportTypesList(response)
                .stream()
                .map(TransportType::getDescription)
                .collect(Collectors.toList());
    }

    private List<TransportType> getTransportTypesList(String response) {
        List<TransportType> transportTypes = new Gson().fromJson(response,
                new TypeReference<List<TransportType>>() {
                }.getType());

        return transportTypes
                .stream()
                .filter(t -> t.getId() != 0 && !t.getDescription().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
