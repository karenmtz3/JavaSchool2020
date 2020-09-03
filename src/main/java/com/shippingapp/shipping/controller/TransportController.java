package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.services.TransportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransportController {
    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/transport/{packageSize}")
    public ResponseEntity<List<String>> getTransportTypesByPackageSize(@PathVariable String packageSize) {
        return new ResponseEntity<>(transportService.getDescriptionForTransportTypes(), HttpStatus.OK);
    }
}
