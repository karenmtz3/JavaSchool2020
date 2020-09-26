package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.models.CityDTO;
import com.shippingapp.shipping.services.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/city")
    public ResponseEntity<List<String>> getCityNames() {
        return new ResponseEntity<>(cityService.getCityNames(), HttpStatus.OK);
    }

    @PostMapping("/cityPath")
    public ResponseEntity<String> getPathFromOriginCityToDestinationCity(@RequestBody CityDTO cityDTO) {
        return new ResponseEntity<>(cityService.getOptimalPath(cityDTO), HttpStatus.OK);
    }
}
