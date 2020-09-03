package com.shippingapp.shipping.exception;

import com.shippingapp.shipping.exception.PackageServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler({PackageServiceException.class})
    public ResponseEntity<String> handlePackageServiceException(PackageServiceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler({TransportServiceException.class})
    public ResponseEntity<String> handleTransportServiceException(TransportServiceException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

}
