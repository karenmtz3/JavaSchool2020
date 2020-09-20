package com.shippingapp.shipping.component;

import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.ResponseIsNullOrEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    private final ConnectionProperties connectionProperties;
    private final AmqpTemplate rabbitTemplate;

    public Request(ConnectionProperties connectionProperties, AmqpTemplate rabbitTemplate) {
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
        return responseVerified(response, message);
    }

    private String responseVerified(Object response, String message) {
        if (Objects.isNull(response) || response.toString().isEmpty()) {
            logger.error("Response of {} is empty or null", message);
            throw new ResponseIsNullOrEmptyException();
        }
        return response.toString();
    }
}
