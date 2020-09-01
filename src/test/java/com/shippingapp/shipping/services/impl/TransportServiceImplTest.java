package com.shippingapp.shipping.services.impl;

import com.shippingapp.shipping.config.Connection;
import com.shippingapp.shipping.exception.TransportServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransportServiceImplTest {
    private String messageType;

    private TransportServiceImpl transportService;
    private Connection connection;
    private AmqpTemplate rabbitTemplate;

    @Before
    public void setUp() {
        this.rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        this.connection = Mockito.mock(Connection.class);

        messageType = "{\"type\":\"transportType\"}";

        transportService = new TransportServiceImpl(rabbitTemplate, connection);
    }

    @Test
    public void getDescriptionForTransportTypes_SuccessExpected() {
        String messageReceived = "[{\"id\":3,\"description\":\"Land\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Arrays.asList("Land", "Air");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionForTransportTypesWithMessageReceivedEmpty_thenThrowTransportServiceException() {
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        assertThatExceptionOfType(TransportServiceException.class).isThrownBy(
                () -> transportService.getDescriptionForTransportTypes());
    }

    @Test
    public void getDescriptionsForTransportTypesWithMessageReceivedNull_thenThrowTransportServiceException() {
        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(null);

        assertThatExceptionOfType(TransportServiceException.class).isThrownBy(
                () -> transportService.getDescriptionForTransportTypes());
    }

    @Test
    public void getDescriptionsForTransportTypesWithAnyValueNullOfMessageReceived_SuccessExpected() {
        String messageReceived = "[{\"id\":null,\"description\":\"Land\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Collections.singletonList("Air");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForTransportTypesWithAnyValueEmptyOfMessageReceived_SuccessExpected() {
        String messageReceived = "[{\"id\":3,\"description\":\"\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Collections.singletonList("Air");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

}
