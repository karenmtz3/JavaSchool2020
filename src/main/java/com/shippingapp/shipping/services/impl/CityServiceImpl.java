package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.component.DfsFindPaths;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.CityServiceException;
import com.shippingapp.shipping.exception.OriginAndDestinationAreEqualsException;
import com.shippingapp.shipping.models.City;
import com.shippingapp.shipping.models.CityPath;
import com.shippingapp.shipping.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    private static final String MESSAGE_CITY = "{\"type\":\"city\"}";
    private static final Type CITY_REFERENCE = new TypeReference<List<City>>() {
    }.getType();
    private static final Type CITY_PATH_REFERENCE = new TypeReference<List<CityPath>>() {
    }.getType();

    private final AmqpTemplate rabbitTemplate;
    private final ConnectionProperties connectionProperties;

    private static final Gson gson = new Gson();

    public CityServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionProperties = connectionProperties;
    }

    public List<String> getCityNames() {
        Object messageResponse = null;
        try {
            messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                    connectionProperties.getRoutingKey(), MESSAGE_CITY);
        } catch (AmqpException ex) {
            handleException(ex.getMessage());
        }

        if (Objects.isNull(messageResponse) || messageResponse.toString().isEmpty()) {
            logger.error("response of cities is empty or null");
            throw new CityServiceException("response of cities is empty or null");
        }
        List<City> cities = gson.fromJson(messageResponse.toString(), CITY_REFERENCE);
        return getCityNamesList(cities);
    }

    private List<String> getCityNamesList(List<City> cities) {
        Set<City> filteredCities = new HashSet<>(cities);
        return filteredCities
                .stream()
                .sorted(Comparator.comparing(City::getName))
                .filter(city -> city.getId() != 0 && !city.getName().isEmpty())
                .map(City::getName)
                .collect(Collectors.toList());
    }

    public String getFirstPath(String origin, String destination) {
        if (!origin.equals(destination)) {
            String message = "{\"type\":\"routesList\"," +
                    "\"origin\":\"" + origin + "\",\"destination\":\"" + destination + "\"}";

            Object messageResponse = null;
            try {
                messageResponse = rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                        connectionProperties.getRoutingKey(), message);
            } catch (Exception ex) {
                handleException(ex.getMessage());
            }

            if (Objects.isNull(messageResponse) || messageResponse.toString().isEmpty()) {
                logger.error("response of city path is empty or null");
                throw new CityServiceException("response of city path is empty or null");
            }
            List<CityPath> cityPaths = gson.fromJson(messageResponse.toString(), CITY_PATH_REFERENCE);
            DfsFindPaths dfsFindPaths = new DfsFindPaths();
            dfsFindPaths.setCityPaths(cityPaths);
            dfsFindPaths.setOrigin(origin);
            dfsFindPaths.setDestination(destination);

            return dfsFindPaths.getFirstPathFromOriginToDestination();
        }
        throw new OriginAndDestinationAreEqualsException("Cities must be different");
    }

    private void handleException(String messageException) {
        logger.error(messageException);
        throw new CentralServerException();
    }
}
