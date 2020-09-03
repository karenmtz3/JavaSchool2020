package com.shippingapp.shipping.services.impl;

import com.shippingapp.shipping.config.ConnectionProperties;
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
    private ConnectionProperties connectionProperties;
    private AmqpTemplate rabbitTemplate;

    @Before
    public void setUp() {
        this.rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        this.connectionProperties = Mockito.mock(ConnectionProperties.class);

        messageType = "{\"type\":\"transportType\"}";

        transportService = new TransportServiceImpl(rabbitTemplate, connectionProperties);
    }

    @Test
    public void getDescriptionForTransportTypes_SuccessExpected() {
        String messageReceived = "[{\"id\":3,\"description\":\"Land\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Arrays.asList("Land", "Air");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionForTransportTypesWithMessageReceivedEmpty_thenThrowTransportServiceException() {
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageType)).thenReturn(messageReceived);

        assertThatExceptionOfType(TransportServiceException.class).isThrownBy(
                () -> transportService.getDescriptionForTransportTypes());
    }

    @Test
    public void getDescriptionsForTransportTypesWithMessageReceivedNull_thenThrowTransportServiceException() {
        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageType)).thenReturn(null);

        assertThatExceptionOfType(TransportServiceException.class).isThrownBy(
                () -> transportService.getDescriptionForTransportTypes());
    }

    @Test
    public void getDescriptionsForTransportTypesWithAnyValueNullOfMessageReceived_SuccessExpected() {
        String messageReceived = "[{\"id\":null,\"description\":\"Land\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Collections.singletonList("Air");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForTransportTypesWithAnyValueEmptyOfMessageReceived_SuccessExpected() {
        String messageReceived = "[{\"id\":3,\"description\":\"\",\"pricePerMile\":2}," +
                "{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

        List<String> responseExpected = Collections.singletonList("Air");

        when(rabbitTemplate.convertSendAndReceive(connectionProperties.getExchange(),
                connectionProperties.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = transportService.getDescriptionForTransportTypes();

        assertThat(response).isEqualTo(responseExpected);
    }

}
