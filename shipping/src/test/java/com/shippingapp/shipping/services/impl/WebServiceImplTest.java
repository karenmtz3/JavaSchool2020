package com.shippingapp.shipping.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.amqp.core.AmqpTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WebServiceImplTest {

    private WebServiceImpl webServiceImpl;

    @Before
    public void setUp(){
        this.webServiceImpl = Mockito.mock(WebServiceImpl.class);
    }

    @Test
    public void getPackagesTypeByCentralServer_SuccessExpected() {
        List<String> responseExpected = Arrays.asList("Envelop", "Box");

        when(webServiceImpl.getPackagesTypeByCentralServer()).thenReturn(responseExpected);

        List<String> response = webServiceImpl.getPackagesTypeByCentralServer();
        assertThat(response).isEqualTo(responseExpected);
    }

    @Test
    public void getPackagesTypeByCentralServer_ReturnEmptyList(){
        when(webServiceImpl.getPackagesTypeByCentralServer()).thenReturn(new ArrayList<>());

        List<String> result = webServiceImpl.getPackagesTypeByCentralServer();
        assertThat(result).isEmpty();
    }
}
