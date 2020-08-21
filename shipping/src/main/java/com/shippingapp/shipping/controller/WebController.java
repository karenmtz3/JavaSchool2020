package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.services.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class WebController {

    private PackageService packageService;

    @GetMapping("/packageType")
    public ResponseEntity<List<String>> getPackagesType() {
        return new ResponseEntity(packageService.getPackagesType(), HttpStatus.OK);
    }

    @Autowired
    public void setPackageService(PackageService packageService){
        this.packageService = packageService;
    }
}
