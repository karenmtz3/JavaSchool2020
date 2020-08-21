package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shippingapp.shipping.config.Connection;
import com.shippingapp.shipping.exception.PackageServiceException;
import com.shippingapp.shipping.models.PackageType;
import com.shippingapp.shipping.services.PackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PackageServiceImpl implements PackageService {

    private static final Logger logger = LoggerFactory.getLogger(PackageServiceImpl.class);

    private final AmqpTemplate rabbitTemplate;
    private final Connection connection;

    private final static String ID = "id";
    private final static String DESCRIPTION = "description";
    private final static String PRICE = "price";

    private List<PackageType> packageTypeList;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, Connection connection) {
        this.rabbitTemplate = rabbitTemplate;
        this.connection = connection;
    }

    public List<String> getPackagesType()  {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "{\"type\":\"packageType\"}";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                        connection.getRoutingKey(), message),
                new TypeReference<String>() {});

        logger.info("response {}",response);
        return getDescriptionsOrNames("");
    }

    private List<String> getDescriptionsOrNames(String response) {
       try {
           JsonArray responseArray = new Gson().
                   fromJson(response, JsonArray.class).getAsJsonArray();

           return createLists(responseArray);
       }
       catch (Exception ex){
           logger.error("Error to get descriptions, unexpected response -> {} ", ex.getMessage());
           throw new PackageServiceException("Error to get type");
       }
    }

    private List<String> createLists(JsonArray packageTypeArray){
        packageTypeList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        for (JsonElement item : packageTypeArray) {
            JsonObject packageType = item.getAsJsonObject();
            int id = packageType.get(ID).getAsInt();
            String description = packageType.get(DESCRIPTION).getAsString();
            int price = packageType.get(PRICE).getAsInt();

            PackageType type = new PackageType(id,description,price);

            packageTypeList.add(type);
            descriptionList.add(description);
        }
        return descriptionList;
    }
}
