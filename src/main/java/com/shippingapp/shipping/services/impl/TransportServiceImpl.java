package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.TransportServiceException;
import com.shippingapp.shipping.models.TransportType;
import com.shippingapp.shipping.models.TransportVelocity;
import com.shippingapp.shipping.services.TransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransportServiceImpl implements TransportService {
    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private static final Type VELOCITY_TYPE_REFERENCE = new TypeReference<List<TransportVelocity>>() {
    }.getType();
    private static final Type TRANSPORT_TYPE_REFERENCE = new TypeReference<List<TransportType>>() {
    }.getType();

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    private static final Gson gson = new Gson();

    public TransportServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
    }

    public List<String> getDescriptionForTransportTypes() {
        String type = "transportType";
        String messageResponse = getMessageResponse(type);

        logger.info("response transport type {}", messageResponse);
        List<TransportType> transportTypes = gson.fromJson(messageResponse, TRANSPORT_TYPE_REFERENCE);
        return getDescriptionTypesList(transportTypes);
    }

    private List<String> getDescriptionTypesList(List<TransportType> transportTypesList) {
        Set<TransportType> transportTypesFiltered = new HashSet<>(transportTypesList);
        return transportTypesFiltered
                .stream()
                .filter(tt -> tt.getId() != 0 && !tt.getDescription().isEmpty())
                .map(TransportType::getDescription)
                .collect(Collectors.toList());
    }

    public List<String> getDescriptionForTransportVelocity() {
        String type = "transportVelocity";
        String messageResponse = getMessageResponse(type);

        logger.info("response transport velocity {}", messageResponse);
        List<TransportVelocity> transportVelocities = gson.fromJson(messageResponse, VELOCITY_TYPE_REFERENCE);
        return getDescriptionVelocitiesList(transportVelocities);
    }

    private List<String> getDescriptionVelocitiesList(List<TransportVelocity> transportVelocities) {
        Set<TransportVelocity> transportVelocityFiltered = new HashSet<>(transportVelocities);
        return transportVelocityFiltered
                .stream()
                .filter(tv -> tv.getId() != 0 && !tv.getDescription().isEmpty())
                .map(TransportVelocity::getDescription)
                .collect(Collectors.toList());
    }

    private String getMessageResponse(String type) {
        String message = "{\"type\":\"" + type + "\"}";
        Object messageResponse;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (AmqpException ex) {
            logger.error(ex.getMessage());
            throw new CentralServerException();
        }

        if (Objects.isNull(messageResponse) || messageResponse.toString().isEmpty()) {
            logger.error("response of {} is null or empty", type);
            throw new TransportServiceException("Error to get " + type);
        }
        return messageResponse.toString();
    }
}
