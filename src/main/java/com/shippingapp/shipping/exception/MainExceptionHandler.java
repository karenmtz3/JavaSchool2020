package com.shippingapp.shipping.exception;

import com.shippingapp.shipping.exception.PackageServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({PackageServiceException.class, TransportServiceException.class})
    public ResponseEntity<String> handlePackageServiceException(Exception e) {
        return error(HttpStatus.BAD_GATEWAY, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        return ResponseEntity.status(status).body(e.getMessage());

    }
}
