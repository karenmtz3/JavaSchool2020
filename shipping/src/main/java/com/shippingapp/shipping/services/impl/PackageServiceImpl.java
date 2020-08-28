package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.shippingapp.shipping.config.Connection;
import com.shippingapp.shipping.exception.PackageServiceException;
import com.shippingapp.shipping.models.PackageSize;
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
    private final static String PRICE_FACTOR = "priceFactor";

    private final List<PackageType> packageTypeList;
    private final List<PackageSize> packageSizeList;

    private final ObjectMapper objectMapper;

    @Autowired
    public PackageServiceImpl(AmqpTemplate rabbitTemplate, Connection connection) {
        this.rabbitTemplate = rabbitTemplate;
        this.connection = connection;
        packageTypeList = new ArrayList<>();
        packageSizeList = new ArrayList<>();
        objectMapper = new ObjectMapper();
    }

    public List<String> getDescriptionsForPackagesType()  {
        String message = "{\"type\":\"packageType\"}";
        String option = "type";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                        connection.getRoutingKey(), message),
                new TypeReference<String>() {});

        logger.info("response package type {}",response);
        return getDescriptions(response, option);
    }

    public List<String> getDescriptionsForPackageSize(String packageType){
        if(packageType.equals(" ")){
            logger.error("packageType can't be empty");
            throw new PackageServiceException("Error to get packages size");
        }
        String message = "{\"type\":\"packageSizeByType\",\"packageType\":\""+packageType+"\"}";
        String option = "size";
        String response = objectMapper.convertValue(
                rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                        connection.getRoutingKey(), message),
                new TypeReference<String>() {});

        logger.info("response package size {}",response);
        return getDescriptions(response, option);

    }

    private List<String> getDescriptions(String response, String option) {
        if(response == null || response.equals("")){
            logger.error("Response can't be empty or null");
            throw new PackageServiceException("Error to get data");
        }
        JsonArray responseArray = new Gson().
                fromJson(response, JsonArray.class).getAsJsonArray();

        return createLists(responseArray, option);

    }

    private List<String> createLists(JsonArray responseArray, String option){
        List<String> descriptionList = new ArrayList<>();
        for (JsonElement element : responseArray) {
            JsonObject item = element.getAsJsonObject();
            if(!item.get(ID).isJsonNull() && !item.get(DESCRIPTION).getAsString().isEmpty()){
                int id = item.get(ID).getAsInt();
                String description = item.get(DESCRIPTION).getAsString();

                if(option.equals("type")) {
                    int price = item.get(PRICE).getAsInt();
                    PackageType type = new PackageType(id,description,price);
                    descriptionList.add(description);

                    boolean packageTypeFound = packageTypeList.stream().
                            anyMatch(pt -> pt.getId() == type.getId());

                    if(!packageTypeFound)
                        packageTypeList.add(type);
                }
                else{
                    int priceFactor = item.get(PRICE_FACTOR).getAsInt();
                    PackageSize size = new PackageSize(id,description,priceFactor);
                    descriptionList.add(description);

                    boolean packageSizeFound = packageSizeList.stream().
                            anyMatch(ps -> ps.getId() == size.getId());

                    if(!packageSizeFound)
                        packageSizeList.add(size);
                }
            }
            else{
                logger.error("JsonObject not added to list, have null values or empty values  -> {}",
                        item);
            }
        }
        return descriptionList;
    }
}
