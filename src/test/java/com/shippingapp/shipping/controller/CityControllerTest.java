package com.shippingapp.shipping.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.CityServiceException;
import com.shippingapp.shipping.exception.OriginAndDestinationAreEqualsException;
import com.shippingapp.shipping.models.CityDTO;
import com.shippingapp.shipping.services.CityService;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CityControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final Gson gson = new Gson();
    private static final String VALID_CITIES = "{\"origin\":\"Chihuahua\", \"destination\":\"Ciudad de Mexico\"}";
    private static final String INVALID_CITIES = "{\"origin\":\"Chihuahua\", \"destination\":\"Chihuahua\"}";
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

        assertThat(receivedList).isEqualTo(expectedList);
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

    @Test
    public void givenValidCities_whenGetPathFromOriginCityToDestinationCity_thenReturnPathAnd200Status() throws Exception {

        String path = "Chihuahua -> Oaxaca -> Tampico -> Tuxtla Gutierrez -> Ciudad de Mexico";
        ArgumentCaptor<CityDTO> cityDTOCaptor = ArgumentCaptor.forClass(CityDTO.class);
        when(cityService.getFirstPath(any(CityDTO.class))).thenReturn(path);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/cityPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CITIES)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(cityService).getFirstPath(cityDTOCaptor.capture());

        assertThat(response.getContentAsString()).isEqualTo(path);
    }

    @Test
    public void givenInvalidCities_whenGetPathFromOriginCityToDestinationCity_thenReject400Status() throws Exception {
        when(cityService.getFirstPath(any(CityDTO.class))).thenThrow(
                new OriginAndDestinationAreEqualsException("Cities must be different"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/cityPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_CITIES)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void whenGetPathFromOriginCityToDestinationCity_thenCentralCommunicationFailsRejectWith417Status() throws Exception {
        when(cityService.getFirstPath(any(CityDTO.class))).thenThrow(
                new CentralServerException());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.post("/cityPath")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CITIES)).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.EXPECTATION_FAILED.value());
    }
}
