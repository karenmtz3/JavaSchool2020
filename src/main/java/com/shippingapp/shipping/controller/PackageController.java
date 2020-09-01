package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.services.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class PackageController {

    private PackageService packageService;

    @GetMapping("/type")
    public ResponseEntity<List<String>> getPackagesType() {
        return new ResponseEntity(packageService.getDescriptionsForPackageTypes(), HttpStatus.OK);
    }

    @GetMapping("/size/{packageType}")
    public ResponseEntity<List<String>> getPackagesSizeByType(@PathVariable String packageType) {
        return new ResponseEntity(packageService.getDescriptionsForPackageSize(packageType), HttpStatus.OK);
    }

    @Autowired
    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }
}
