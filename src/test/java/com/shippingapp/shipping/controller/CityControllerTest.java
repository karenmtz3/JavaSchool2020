package com.shippingapp.shipping.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.CityServiceException;
import com.shippingapp.shipping.services.CityService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CityControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final TypeReference<List<String>> CITIES_NAMES_REFERENCE = new TypeReference<List<String>>() {
    };

    @MockBean
    private CityService cityService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void whenGetCityNames_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList = Arrays.asList("La Paz", "Mexicali", "Hermosillo", "Culiacan", "Tepic");

        when(cityService.getCityNames()).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/city"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(cityService).getCityNames();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(), CITIES_NAMES_REFERENCE);

        AssertionsForInterfaceTypes.assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void whenGetCityNames_thenReturnEmptyListAnd200Status() throws Exception {
        when(cityService.getCityNames()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/city"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(cityService).getCityNames();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(), CITIES_NAMES_REFERENCE);

        AssertionsForInterfaceTypes.assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenInvalidResponse_whenGetCityNames_thenRejectWith409Status() throws Exception {
        when(cityService.getCityNames()).thenThrow(
                new CityServiceException("Error to get cities"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/city"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void whenGetCityNames_thenCentralCommunicationFailsRejectWith417Status() throws Exception {
        when(cityService.getCityNames()).thenThrow(
                new CentralServerException());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/city"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.EXPECTATION_FAILED.value());
    }
}
