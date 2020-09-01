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
public class PackageControllerTest {

    private MockMvc mockMvc;
    private final static String PACKAGE_TYPE = "Box";
    private ObjectMapper objectMapper;

    @MockBean
    private PackageService packageService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void whenGetDescriptionsForPackageTypes_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList = Arrays.asList("Envelop", "Box");

        when(packageService.getDescriptionsForPackageTypes()).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/type"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getDescriptionsForPackageTypes();


        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void whenGetDescriptionsForPackageTypes_thenReturnEmptyListAnd200Status() throws Exception {
        when(packageService.getDescriptionsForPackageTypes()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/type"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getDescriptionsForPackageTypes();

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenInvalidResponse_whenGetDescriptionsForPackageTypes_thenRejectWith502Status() throws Exception {
        when(packageService.getDescriptionsForPackageTypes()).
                thenThrow(new PackageServiceException("Error to get type"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/type"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
    }

    @Test
    public void givenInvalidPackageType_whenGetDescriptionsForPackageSizes_thenReturnEmptyList() throws Exception {
        String packageType = "some";
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/size/" + packageType))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getDescriptionsForPackageSize(packageType);

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void notGivenAPackageType_whenGetDescriptionsForPackageSizes_thenRejectWith404Status() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/size/"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void givenAPackageType_whenGetDescriptionsForPackageSizes_thenReturnListAnd200Status() throws Exception {
        List<String> expectedList = Arrays.asList("Small", "Medium", "Large");

        when(packageService.getDescriptionsForPackageSize(PACKAGE_TYPE)).thenReturn(expectedList);

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/size/" + PACKAGE_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getDescriptionsForPackageSize(PACKAGE_TYPE);

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEqualTo(expectedList);
    }

    @Test
    public void givenAPackageType_whenGetDescriptionsForPackageSizes_thenReturnEmptyListAnd200Status() throws Exception {
        when(packageService.getDescriptionsForPackageSize(PACKAGE_TYPE)).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/size/" + PACKAGE_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(packageService).getDescriptionsForPackageSize(PACKAGE_TYPE);

        List<String> receivedList = objectMapper.readValue(response.getContentAsString(),
                new TypeReference<List<String>>() {
                });

        assertThat(receivedList).isEmpty();
    }

    @Test
    public void givenAPackageTypeWithInvalidResponse_whenGetDescriptionsForPackageSizes_thenRejectWith502Status() throws Exception {
        when(packageService.getDescriptionsForPackageSize(PACKAGE_TYPE)).thenThrow(new PackageServiceException("Error to get size"));

        MockHttpServletResponse response = mockMvc.perform(
                MockMvcRequestBuilders.get("/size/" + PACKAGE_TYPE))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
    }
}
