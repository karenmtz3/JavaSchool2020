package com.shippingapp.shipping.services.impl;

import com.google.gson.Gson;
import com.shippingapp.shipping.config.ConnectionProperties;
import com.shippingapp.shipping.exception.CityServiceException;
import com.shippingapp.shipping.models.CityDTO;
import com.shippingapp.shipping.services.CityService;
import com.shippingapp.shipping.services.OptimalPathService;
import com.shippingapp.shipping.util.MessageLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceImplTest {
    private String messageCity;
    private String messagePath;
    private CityDTO cityDTO;

    private static final Gson gson = new Gson();
    private final static String ORIGIN = "Chihuahua";
    private final static String DESTINATION = "Ciudad de Mexico";
    private static final String VALID_CITIES = "{\"origin\":\"Chihuahua\", \"destination\":\"Ciudad de Mexico\"}";


    private CityService cityService;
    private ConnectionProperties connectionProperties;
    private AmqpTemplate rabbitTemplate;

    @Before
    public void setUp() {
        this.rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        this.connectionProperties = Mockito.mock(ConnectionProperties.class);
        OptimalPathService optimalPathService = new OptimalPathServiceImpl();

        messageCity = "{\"type\":\"city\"}";
        messagePath = "{\"type\":\"routesList\",\"origin\":\"Chihuahua\",\"destination\":\"Ciudad de Mexico\"}";
        cityDTO = gson.fromJson(VALID_CITIES, CityDTO.class);
        cityService = new CityServiceImpl(rabbitTemplate, connectionProperties, optimalPathService);
    }

    @Test
    public void getCityNames_SuccessExpected() {
        String messageReceived = " [{\"id\":33,\"name\":\"La Paz\",\"tax\":10,\"seaport\":true,\"airport\":true}," +
                "{\"id\":34,\"name\":\"Mexicali\",\"tax\":5,\"seaport\":false,\"airport\":true}," +
                "{\"id\":35,\"name\":\"Hermosillo\",\"tax\":15,\"seaport\":false,\"airport\":true}," +
                "{\"id\":36,\"name\":\"Culiacan\",\"tax\":10,\"seaport\":false,\"airport\":true}," +
                "{\"id\":37,\"name\":\"Tepic\",\"tax\":5,\"seaport\":false,\"airport\":true}]";

        List<String> responseExpected = Arrays.asList("Culiacan", "Hermosillo", "La Paz", "Mexicali", "Tepic");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageCity)).thenReturn(messageReceived);

        List<String> response = cityService.getCityNames();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getCityNamesWithMessageReceivedEmpty_thenThrowCityServiceException() {
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageCity)).thenReturn(messageReceived);

        assertThatExceptionOfType(CityServiceException.class).isThrownBy(
                () -> cityService.getCityNames());
    }

    @Test
    public void getCityNamesWithMessageReceivedNull_thenThrowCityServiceException() {
        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageCity)).thenReturn(null);

        assertThatExceptionOfType(CityServiceException.class).isThrownBy(
                () -> cityService.getCityNames());
    }

    @Test
    public void getCityNamesWithAnyValueNullOfMessageReceived_SuccessExpected() {
        String messageReceived = " [{\"id\":null,\"name\":\"La Paz\",\"tax\":10,\"seaport\":true,\"airport\":true}," +
                "{\"id\":34,\"name\":\"Mexicali\",\"tax\":5,\"seaport\":false,\"airport\":true}," +
                "{\"id\":35,\"name\":\"Hermosillo\",\"tax\":15,\"seaport\":false,\"airport\":true}," +
                "{\"id\":36,\"name\":\"Culiacan\",\"tax\":10,\"seaport\":false,\"airport\":true}," +
                "{\"id\":null,\"name\":\"Tepic\",\"tax\":5,\"seaport\":false,\"airport\":true}]";

        List<String> responseExpected = Arrays.asList("Culiacan", "Hermosillo", "Mexicali");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageCity)).thenReturn(messageReceived);

        List<String> response = cityService.getCityNames();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getCityNamesWithAnyValueEmptyOfMessageReceived_SuccessExpected() {
        String messageReceived = " [{\"id\":33,\"name\":\"La Paz\",\"tax\":10,\"seaport\":true,\"airport\":true}," +
                "{\"id\":34,\"name\":\"\",\"tax\":5,\"seaport\":false,\"airport\":true}," +
                "{\"id\":35,\"name\":\"Hermosillo\",\"tax\":15,\"seaport\":false,\"airport\":true}," +
                "{\"id\":36,\"name\":\"\",\"tax\":10,\"seaport\":false,\"airport\":true}," +
                "{\"id\":37,\"name\":\"\",\"tax\":5,\"seaport\":false,\"airport\":true}]";

        List<String> responseExpected = Arrays.asList("Hermosillo", "La Paz");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageCity)).thenReturn(messageReceived);

        List<String> response = cityService.getCityNames();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getOptimalPathWithoutIntermediateCities_SuccessExpected() throws IOException {
        String messageReceived = MessageLoader.loadExampleResponse("responses/city-path-without-intermediate-cities.json");

        String pathExpected = "Chihuahua -> Ciudad de Mexico";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(messageReceived);

        String response = cityService.getOptimalPath(cityDTO);

        assertThat(response).isEqualTo(pathExpected);
    }

    @Test
    public void getOptimalPathWithSingleIntermediateCity_SuccessExpected() throws IOException {
        String messageReceived = MessageLoader.loadExampleResponse("responses/city-path-with-single-intermediate-city.json");

        String pathExpected = "Chihuahua -> Torreon -> Ciudad de Mexico";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(messageReceived);

        String response = cityService.getOptimalPath(cityDTO);

        assertThat(response).isEqualTo(pathExpected);
    }

    @Test
    public void getOptimalPathWithTwoIntermediateCities_SuccessExpected() throws IOException {
        String messageReceived = MessageLoader.loadExampleResponse("responses/city-path-with-two-intermediate-cities.json");


        String pathExpected = "Chihuahua -> Durango -> Ciudad del Carmen -> Ciudad de Mexico";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(messageReceived);

        String response = cityService.getOptimalPath(cityDTO);

        assertThat(response).isEqualTo(pathExpected);
    }

    @Test
    public void getOptimalPathWithThreeIntermediateCities_SuccessExpected() throws IOException {
        String messageReceived = MessageLoader.loadExampleResponse("responses/city-path-with-three-intermediate-cities.json");

        String pathExpected = "Chihuahua -> Cancun -> Toluca -> Merida -> Ciudad de Mexico";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(messageReceived);

        String response = cityService.getOptimalPath(cityDTO);

        assertThat(response).isEqualTo(pathExpected);
    }

    @Test
    public void getFirstPathWithMessageReceivedEmpty_thenThrowCityServiceException() {
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(messageReceived);

        assertThatExceptionOfType(CityServiceException.class).isThrownBy(
                () -> cityService.getOptimalPath(cityDTO));
    }

    @Test
    public void getFirstPathWithMessageReceivedNull_thenThrowCityServiceException() {
        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messagePath)).thenReturn(null);

        assertThatExceptionOfType(CityServiceException.class).isThrownBy(
                () -> cityService.getOptimalPath(cityDTO));
    }
}