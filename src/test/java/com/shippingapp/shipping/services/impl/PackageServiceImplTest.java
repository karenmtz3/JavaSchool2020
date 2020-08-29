package com.shippingapp.shipping.services.impl;

import com.shippingapp.shipping.config.Connection;
import com.shippingapp.shipping.exception.PackageServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PackageServiceImplTest {

    private String message;
    private PackageServiceImpl packageService;
    private Connection connection;
    private AmqpTemplate rabbitTemplate;

    @Before
    public void setUp(){
        this.rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        this.connection = Mockito.mock(Connection.class);

        message = "{\"type\":\"packageType\"}";

        packageService = new PackageServiceImpl(rabbitTemplate,connection);
    }

    @Test
    public void getDescriptionsList_SuccessExpected() {
        String messageReceived  = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Arrays.asList("Envelop", "Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                        connection.getRoutingKey(), message)).thenReturn(messageReceived);

        List<String> response = packageService.getPackageTypeDescriptions();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsListWithMessageReceivedEmpty_ThenThrowPackageServiceException(){
       String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), message)).thenReturn(messageReceived);

       assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
               () -> packageService.getPackageTypeDescriptions());
    }

    @Test
    public void getDescriptionsListWithMessageReceivedNull_ThenThrowPackageServiceException(){
        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), message)).thenReturn(null);

        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getPackageTypeDescriptions());
    }

    @Test
    public void getDescriptionsListWithAnyValueNullOfMessageReceived_SuccessExpected(){
        String messageReceived  = "[{\"id\":null,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Collections.singletonList("Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), message)).thenReturn(messageReceived);

        List<String> response = packageService.getPackageTypeDescriptions();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsListWithAnyValueEmptyOfMessageReceived_SuccessExpected(){
        String messageReceived  = "[{\"id\":null,\"description\":\"\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Collections.singletonList("Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), message)).thenReturn(messageReceived);

        List<String> response = packageService.getPackageTypeDescriptions();

        assertThat(response).isEqualTo(responseExpected);
    }
}
