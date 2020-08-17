package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WebServiceImpl implements WebService {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceImpl.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    private List<PackageType> packageTypeList;

    @Value("${spring.rabbitmq.template.exchange}")
    String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingkey;

    public List<String> getPackagesTypeByCentralServer(){
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "{\"type\":\"packageType\"}";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(exchange, routingkey, message),
                new TypeReference<String>() {});

        return getDescriptionOrName(response);
    }

    private List<String> getDescriptionOrName(String response){
        List<String> descriptionList = new ArrayList<>();
        try{
            JsonArray responseArray = new Gson().
                    fromJson(response, JsonArray.class).getAsJsonArray();
            String key = "description";
            createPackageTypeList(responseArray);

            for (JsonElement item : responseArray) {
                JsonObject packageType = item.getAsJsonObject();
                String description = packageType.get(key).getAsString();
                descriptionList.add(description);
            }

        }catch (Exception ex){
            logger.error("Error to get description {}", ex.getMessage());
        }
        return descriptionList;
    }

    private void createPackageTypeList(JsonArray packageTypeArray){
        packageTypeList = new ArrayList<>();
        for (JsonElement item : packageTypeArray) {
            JsonObject packageType = item.getAsJsonObject();
            int id = packageType.get("id").getAsInt();
            String description = packageType.get("description").getAsString();
            int price = packageType.get("price").getAsInt();
            PackageType type = new PackageType(id,description,price);
            packageTypeList.add(type);
        }
    }
}
