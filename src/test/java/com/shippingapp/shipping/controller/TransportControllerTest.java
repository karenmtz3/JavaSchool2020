package com.shippingapp.shipping.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shippingapp.shipping.exception.CentralServerException;
import com.shippingapp.shipping.exception.TransportServiceException;
import com.shippingapp.shipping.services.TransportService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransportControllerTest {
    private MockMvc mockMvc;
    private final static String PACKAGE_SIZE = "Small";
    private final static String TRANSPORT_TYPE = "Land";
    private ObjectMapper objectMapper;

    @MockBean
    private TransportService transportService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void whenGetDescriptionsForTransportType_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList = Arrays.asList("Land", "Air");

        when(transportService.getDescriptionForTransportTypes()).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/transport/" + PACKAGE_SIZE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transportService).getDescriptionForTransportTypes();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void whenGetDescriptionsForTransportType_thenReturnEmptyListAnd200Status() throws Exception {
        when(transportService.getDescriptionForTransportTypes()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/transport/" + PACKAGE_SIZE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transportService).getDescriptionForTransportTypes();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenInvalidResponse_whenGetDescriptionsForTransportType_thenRejectWith409Status() throws Exception {
        when(transportService.getDescriptionForTransportTypes()).thenThrow(
                new TransportServiceException("Error to get type..."));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/transport/" + PACKAGE_SIZE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void whenGetDescriptionsForTransportType_thenCentralCommunicationFailsRejectWith500Status() throws Exception {
        when(transportService.getDescriptionForTransportTypes()).thenThrow(
                new CentralServerException());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/transport/" + PACKAGE_SIZE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    public void whenGetDescriptionForTransportVelocity_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList = Arrays.asList("Regular", "Express", "Following day");

        when(transportService.getDescriptionForTransportVelocity()).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/time/" + TRANSPORT_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transportService).getDescriptionForTransportVelocity();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void whenGetDescriptionForTransportVelocities_thenReturnEmptyListAnd200Status() throws Exception {
        when(transportService.getDescriptionForTransportTypes()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/time/" + TRANSPORT_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(transportService).getDescriptionForTransportVelocity();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenInvalidResponse_thenGetDescriptionFotTransportVelocities_thenRejectWith409Status() throws Exception {
        when(transportService.getDescriptionForTransportVelocity()).thenThrow(
                new TransportServiceException("Error to get transport velocities"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/time/" + TRANSPORT_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    public void whenGetDescriptionsForTransportVelocities_thenCentralCommunicationFailsRejectWith500Status() throws Exception {
        when(transportService.getDescriptionForTransportVelocity()).thenThrow(
                new CentralServerException());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/time/" + TRANSPORT_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
