package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
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
import java.util.Optional;


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
        packageTypeList = new ArrayList<>();
    }

    public List<String> getDescriptionsList()  {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = "{\"type\":\"packageType\"}";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                        connection.getRoutingKey(), message),
                new TypeReference<String>() {});

        logger.info("response {}",response);
        return getDescriptionsOrNames(response);
    }

    private List<String> getDescriptionsOrNames(String response) {
        if(response.equals("")){
            logger.error("Response can't be empty");
            throw new PackageServiceException("Error to get type");
        }

        try {
           JsonArray responseArray = new Gson().
                   fromJson(response, JsonArray.class).getAsJsonArray();

           return createLists(responseArray);
        }
        catch (NullPointerException e){
           logger.error("Response can't be null");
           throw new PackageServiceException("Error to get type");
        }
    }

    private List<String> createLists(JsonArray packageTypeArray){
        List<String> descriptionList = new ArrayList<>();
        for (JsonElement item : packageTypeArray) {
            JsonObject packageType = item.getAsJsonObject();
            if(!packageType.get(ID).isJsonNull() && !packageType.get(DESCRIPTION).equals("")){
                int id = packageType.get(ID).getAsInt();
                String description = packageType.get(DESCRIPTION).getAsString();
                int price = packageType.get(PRICE).getAsInt();

                PackageType type = new PackageType(id,description,price);

                descriptionList.add(description);
                PackageType packageTypeOptional = packageTypeList.stream().
                        filter(pt -> pt.getId() ==  type.getId()).findFirst().orElse(null);

                if(packageTypeOptional == null)
                    packageTypeList.add(type);
            }
            else{
                logger.error("JsonObject not added to list, have null values or empty values  -> {}",
                        packageType);
            }
        }
        return descriptionList;
    }
}
