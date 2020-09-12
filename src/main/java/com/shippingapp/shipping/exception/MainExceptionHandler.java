package com.shippingapp.shipping.exception;

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

    @ExceptionHandler({CentralServerException.class})
    public ResponseEntity<String> handleCentralServerException(CentralServerException e) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
    }

    @ExceptionHandler({PackageTypeIsNullOrEmptyException.class})
    public ResponseEntity<String> handlePackageTypeIsNullOrEmptyException(PackageTypeIsNullOrEmptyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({TransportServiceException.class})
    public ResponseEntity<String> handleTransportServiceException(TransportServiceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler({CityServiceException.class})
    public ResponseEntity<String> handleCityServiceException(CityServiceException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler({OriginAndDestinationAreEqualsException.class})
    public ResponseEntity<String> handleOriginAndDestinationAreEqualsException(OriginAndDestinationAreEqualsException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
