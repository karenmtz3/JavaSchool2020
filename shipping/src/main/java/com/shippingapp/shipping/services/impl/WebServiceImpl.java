package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.services.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class WebServiceImpl implements WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue}")
    String queueName;

    @Value("${spring.rabbitmq.template.exchange}")
    String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingkey;

    public WebServiceImpl(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRequest(){
        String body = "{\"type\":\"packageType\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(exchange, routingkey, body),
                new TypeReference<String>() {});

        logger.info("send msg = {}", body);
        logger.info("response = {}", response);
    }
}
