package com.hubpay.digitalwallet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hubpay.digitalwallet.exception.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String err = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).findFirst().orElse("unkown");
        return new ResponseEntity<>(
                ErrorResponse.builder().errorCode(HttpStatus.BAD_REQUEST.value()).errorMessage(err).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentErrors(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().errorCode(HttpStatus.BAD_REQUEST.value()).errorMessage(ex.getMessage()).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerErrors(Exception ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder().errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errorMessage(String.format("unexpected error occured: %s", ex.getMessage())).build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
