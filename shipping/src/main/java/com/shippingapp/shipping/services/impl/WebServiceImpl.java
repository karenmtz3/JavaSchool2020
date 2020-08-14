package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.dto.RequestType;
import com.shippingapp.shipping.services.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebServiceImpl implements WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceImpl.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queue}")
    String queueName;

    @Value("${spring.rabbitmq.template.exchange}")
    String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingkey;

    public void sendRequest(RequestType requestType){

        ObjectMapper objectMapper = new ObjectMapper();
        Object response = rabbitTemplate.convertSendAndReceive(exchange,routingkey,requestType);
        logger.info("send msg = {}", requestType.toString());
        logger.info("response = {}", response);

        /*rabbitTemplate.convertAndSend(exchange,routingkey,typeResponseDTO);
        RequestType response = objectMapper.convertValue(rabbitTemplate.receiveAndConvert(queueName),
                new TypeReference<RequestType>() {});

        logger.info("response = {}", response.toString());*/
    }
}
