package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.services.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/city")
    public ResponseEntity<List<String>> getCitiesNames() {
        return new ResponseEntity<>(cityService.getCitiesNames(), HttpStatus.OK);
    }
}
