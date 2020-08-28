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

    private String messageType;
    private String messageSize;
    private final static String PACKAGE_TYPE = "Box";

    private PackageServiceImpl packageService;
    private Connection connection;
    private AmqpTemplate rabbitTemplate;


    @Before
    public void setUp(){
        this.rabbitTemplate = Mockito.mock(AmqpTemplate.class);
        this.connection = Mockito.mock(Connection.class);

        messageType = "{\"type\":\"packageType\"}";
        messageSize = "{\"type\":\"packageSizeByType\",\"packageType\":\"" + PACKAGE_TYPE + "\"}";

        packageService = new PackageServiceImpl(rabbitTemplate,connection);
    }

    @Test
    public void getDescriptionsForPackagesType_SuccessExpected() {
        String messageReceived  = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Arrays.asList("Envelop", "Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackagesType();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForPackagesTypeWithMessageReceivedEmpty_ThenThrowPackageServiceException(){
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getDescriptionsForPackagesType());
    }

    @Test
    public void getDescriptionsForPackagesTypeListWithMessageReceivedNull_ThenThrowPackageServiceException(){
        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(null);

        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getDescriptionsForPackagesType());
    }

    @Test
    public void getDescriptionsForPackagesTypeWithAnyValueNullOfMessageReceived_SuccessExpected(){
        String messageReceived  = "[{\"id\":null,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Collections.singletonList("Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackagesType();

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForPackagesTypeWithAnyValueEmptyOfMessageReceived_SuccessExpected(){
        String messageReceived  = "[{\"id\":3,\"description\":\"\",\"price\":5},{\"id\":4,\"" +
                "description\":\"Box\",\"price\":12}]";

        List<String> responseExpected = Collections.singletonList("Box");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageType)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackagesType();

        assertThat(response).isEqualTo(responseExpected);
    }



    @Test
    public void getDescriptionsForPackageSize_SuccessExpected(){
        String messageReceived = "[{\"id\":4,\"description\":\"Small\",\"priceFactor\":10}," +
                "{\"id\":5,\"description\":\"Medium\",\"priceFactor\":25}," +
                "{\"id\":6,\"description\":\"Large\",\"priceFactor\":50}]";

        List<String> responseExpected = Arrays.asList("Small", "Medium", "Large");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageSize)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackageSize(PACKAGE_TYPE);

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForPackageSizeWithMessageReceivedEmpty_ThenThrowPackageServiceException(){
        String messageReceived = "";

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageSize)).thenReturn(messageReceived);

        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getDescriptionsForPackageSize(PACKAGE_TYPE));
    }

    @Test
    public void getDescriptionsForPackageSizeListWithMessageReceivedNull_ThenThrowPackageServiceException(){
        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageSize)).thenReturn(null);

        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getDescriptionsForPackageSize(PACKAGE_TYPE));
    }

    @Test
    public void getDescriptionsForPackageSizeWithPackageTypeEmpty_ThenThrowPackageServiceException(){
        assertThatExceptionOfType(PackageServiceException.class).isThrownBy(
                () -> packageService.getDescriptionsForPackageSize(" "));
    }

    @Test
    public void getDescriptionsForPackageSizeWithAnyValueNullOfMessageReceived_SuccessExpected(){
        String messageReceived = "[{\"id\":null,\"description\":\"Small\",\"priceFactor\":10}," +
                "{\"id\":5,\"description\":\"Medium\",\"priceFactor\":25}," +
                "{\"id\":null,\"description\":\"Large\",\"priceFactor\":50}]";

        List<String> responseExpected = Collections.singletonList("Medium");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageSize)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackageSize(PACKAGE_TYPE);

        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getDescriptionsForPackageSizeWithAnyValueEmptyOfMessageReceived_SuccessExpected(){
        String messageReceived = "[{\"id\":4,\"description\":\"\",\"priceFactor\":10}," +
                "{\"id\":5,\"description\":\"Medium\",\"priceFactor\":25}," +
                "{\"id\":6,\"description\":\"Large\",\"priceFactor\":50}]";

        List<String> responseExpected = Arrays.asList("Medium", "Large");

        when(rabbitTemplate.convertSendAndReceive(connection.getExchange(),
                connection.getRoutingKey(), messageSize)).thenReturn(messageReceived);

        List<String> response = packageService.getDescriptionsForPackageSize(PACKAGE_TYPE);

        assertThat(response).isEqualTo(responseExpected);
    }
}
