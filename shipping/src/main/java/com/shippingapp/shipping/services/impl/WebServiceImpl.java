package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.WebService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WebServiceImpl implements WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private List<PackageType> packageTypeList;

    @Value("${spring.rabbitmq.queue}")
    String queueName;

    @Value("${spring.rabbitmq.template.exchange}")
    String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingkey;

    public WebServiceImpl(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        objectMapper = new ObjectMapper();
    }

    public List<String> getPackagesTypeByCentralServer(){
        String message = "{\"type\":\"packageType\"}";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(exchange, routingkey, message),
                new TypeReference<String>() {});

        return getDescriptionOrName(response);
    }

    private List<String> getDescriptionOrName(String response){
        List<String> descriptionList = new ArrayList<>();
        try{
            JSONArray responseArray = new JSONArray(response);
            String key = "description";
            createPackageTypeList(responseArray);

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject packageType = responseArray.getJSONObject(i);
                String description = packageType.getString(key);
                descriptionList.add(description);
            }

        }catch (Exception ex){
            logger.error("Error to get description {}", ex.getMessage());
        }
        return descriptionList;
    }

    private void createPackageTypeList(JSONArray packageTypeArray){
        packageTypeList = new ArrayList<>();

        for (int i = 0; i < packageTypeArray.length(); i++) {
            JSONObject packageTypeJSON = packageTypeArray.getJSONObject(i);
            int id = packageTypeJSON.getInt("id");
            String description = packageTypeJSON.getString("description");
            int price = packageTypeJSON.getInt("price");
            PackageType type = new PackageType(id,description,price);
            packageTypeList.add(type);
        }
    }
}
