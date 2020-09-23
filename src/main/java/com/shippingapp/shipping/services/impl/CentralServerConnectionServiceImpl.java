package com.shippingapp.shipping.services.impl;

import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.ResponseIsNullOrEmptyException;
import com.shippingapp.shipping.services.CentralServerConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CentralServerConnectionServiceImpl implements CentralServerConnectionService {
    private static final Logger logger = LoggerFactory.getLogger(CentralServerConnectionServiceImpl.class);

    private final ConnectionProperties connectionProperties;
    private final AmqpTemplate rabbitTemplate;

    public CentralServerConnectionServiceImpl(ConnectionProperties connectionProperties, AmqpTemplate rabbitTemplate) {
        this.connectionProperties = connectionProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String sendRequestAndReceiveResponse(String message) {
        Object response;
        try {
            response = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (AmqpException ex) {
            logger.error(ex.getMessage());
            throw new CentralServerException();
        }
        return verifyResponse(response, message);
    }

    private String verifyResponse(Object response, String message) {
        if (Objects.isNull(response) || response.toString().isEmpty()) {
            logger.error("Response of {} is empty or null", message);
            throw new ResponseIsNullOrEmptyException();
        }
        return response.toString();
    }
}
