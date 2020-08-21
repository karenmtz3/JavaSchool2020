package com.shippingapp.shipping.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.exception.PackageServiceException;
import com.shippingapp.shipping.services.PackageService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WebControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private PackageService packageService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenListPackageType_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList =  Arrays.asList("Envelop", "Box");

        when(packageService.getPackagesType()).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/packageType"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getPackagesType();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {});

        assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void whenListPackageType_thenReturnEmptyListAnd200Status() throws Exception {
        when(packageService.getPackagesType()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/packageType"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getPackagesType();

        ObjectMapper objectMapper = new ObjectMapper();
        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {});

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenInvalidResponse_whenListPackageType_thenRejectWith417Status() throws Exception{
        when(packageService.getPackagesType()).thenThrow(new PackageServiceException("Error to get type"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/packageType"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.EXPECTATION_FAILED.value());
    }
}
