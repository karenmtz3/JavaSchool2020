package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.TransportServiceException;
import com.shippingapp.shipping.models.TransportType;
import com.shippingapp.shipping.services.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportServiceImpl implements TransportService {
    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    public TransportServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
    }

    public List<String> getDescriptionForTransportTypes() {
        String message = "{\"type\":\"transportType\"}";
        Object messageResponse;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (Exception ex) {
            throw new CentralServerException("Central server can't get response");
        }

        logger.info("response transport type {}", messageResponse);
        if (messageResponse == null || messageResponse.toString().isEmpty()) {
            logger.error("response of transport type is null or empty");
            throw new TransportServiceException("Error to get transport types");
        }
        List<TransportType> transportTypes = new Gson().fromJson(messageResponse.toString(),
                new TypeReference<List<TransportType>>() {
                }.getType());
        return getDescriptionTypesList(transportTypes);
    }

    private List<String> getDescriptionTypesList(List<TransportType> transportTypesList) {
        return transportTypesList
                .stream()
                .filter(tt -> tt.getId() != 0 && !tt.getDescription().isEmpty())
                .distinct()
                .map(TransportType::getDescription)
                .collect(Collectors.toList());
    }
}
