package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.services.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private WebService webService;

    @GetMapping("/sendmessage")
    public ResponseEntity<String> getPackages(){
        webService.sendRequest();
        return new ResponseEntity("send", HttpStatus.OK);
    }

    @Autowired
    public void setWebService(WebService webService){
        this.webService = webService;
    }
}
