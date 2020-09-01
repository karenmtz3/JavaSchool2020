package com.shippingapp.shipping.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({PackageServiceException.class})
    public ResponseEntity<String> handlePackageServiceException(PackageServiceException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}
