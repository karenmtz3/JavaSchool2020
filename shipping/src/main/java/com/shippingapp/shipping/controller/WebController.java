package com.shippingapp.shipping.controller;

import com.shippingapp.shipping.dto.RequestType;
import com.shippingapp.shipping.services.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private WebService webService;
    private RequestType requestType;

    @GetMapping("/sendmessage")
    public ResponseEntity<String> getPackages(){
        requestType = new RequestType();
        requestType.setType("packageType");
        webService.sendRequest(requestType);
        return new ResponseEntity(requestType, HttpStatus.OK);
    }

    @Autowired
    public void setWebService(WebService webService){
        this.webService = webService;
    }
}
