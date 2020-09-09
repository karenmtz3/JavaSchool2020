package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.CityServiceException;
import com.shippingapp.shipping.models.City;
import com.shippingapp.shipping.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);
    private static final Type CITY_REFERENCE = new TypeReference<List<City>>() {
    }.getType();

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    private static final Gson gson = new Gson();

    public CityServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
    }

    public List<String> getCitiesNames() {
        String message = "{\"type\":\"city\"}";
        Object messageResponse;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), message);
        } catch (AmqpException ex) {
            logger.error(ex.getMessage());
            throw new CentralServerException();
        }

        if (Objects.isNull(messageResponse) || messageResponse.toString().isEmpty()) {
            logger.error("response of cities is empty or null");
            throw new CityServiceException("response of cities is empty or null");
        }
        List<City> cities = gson.fromJson(messageResponse.toString(), CITY_REFERENCE);
        return getCitiesNamesList(cities);
    }

    private List<String> getCitiesNamesList(List<City> cities) {
        Set<City> citiesFiltered = new HashSet<>(cities);
        return citiesFiltered
                .stream()
                .sorted(Comparator.comparing(City::getName))
                .filter(city -> city.getId() != 0 && !city.getName().isEmpty())
                .map(City::getName)
                .collect(Collectors.toList());
    }
}
