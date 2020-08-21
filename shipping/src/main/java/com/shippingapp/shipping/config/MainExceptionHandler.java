package com.shippingapp.shipping.config;

import com.shippingapp.shipping.exception.PackageServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({PackageServiceException.class})
    public ResponseEntity<String> handlePackageServiceException(PackageServiceException e) {
        return error(HttpStatus.EXPECTATION_FAILED, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        return ResponseEntity.status(status).body(e.getMessage());

    }
}
