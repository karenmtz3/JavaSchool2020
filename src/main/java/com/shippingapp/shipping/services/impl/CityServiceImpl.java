package com.shippingapp.shipping.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.shippingapp.shipping.component.DfsFindPaths;
import com.shippingapp.shipping.component.Request;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.OriginAndDestinationAreEqualsException;
import com.shippingapp.shipping.models.City;
import com.shippingapp.shipping.models.CityDTO;
import com.shippingapp.shipping.models.CityPath;
import com.shippingapp.shipping.services.CityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    private static final String MESSAGE_CITY = "{\"type\":\"city\"}";
    private static final String MESSAGE_CITY_PATH = "{\"type\":\"routesList\"," +
            "\"origin\":\"%s\",\"destination\":\"%s\"}";
    private static final Type CITY_REFERENCE = new TypeReference<List<City>>() {
    }.getType();
    private static final Type CITY_PATH_REFERENCE = new TypeReference<List<CityPath>>() {
    }.getType();

    private final Request request;
    private final DfsFindPaths dfsFindPaths;
    private static final Gson gson = new Gson();

    public CityServiceImpl(AmqpTemplate rabbitTemplate, ConnectionProperties connectionProperties, DfsFindPaths dfsFindPaths) {
        request = new Request(connectionProperties, rabbitTemplate);
        this.dfsFindPaths = dfsFindPaths;
    }

    public List<String> getCityNames() {
        String messageResponse = request.sendRequestAndReceiveResponse(MESSAGE_CITY);

        List<City> cities = gson.fromJson(messageResponse, CITY_REFERENCE);
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

    public String getFirstPath(CityDTO cityDTO) {
        if (!cityDTO.getOrigin().equals(cityDTO.getDestination())) {
            String message = String.format(MESSAGE_CITY_PATH,
                    cityDTO.getOrigin(), cityDTO.getDestination());

            String messageResponse = request.sendRequestAndReceiveResponse(message);
            List<CityPath> cityPaths = gson.fromJson(messageResponse, CITY_PATH_REFERENCE);

            return dfsFindPaths.getFirstPathFromOriginToDestination(cityPaths, cityDTO.getOrigin(), cityDTO.getDestination());
        }
        throw new OriginAndDestinationAreEqualsException("Cities must be different");
    }
}
