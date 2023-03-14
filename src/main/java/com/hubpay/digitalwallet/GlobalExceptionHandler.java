package com.hubpay.digitalwallet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hubpay.digitalwallet.exception.ErrorResponse;
import com.hubpay.digitalwallet.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ServiceException.class)
        public ResponseEntity<ErrorResponse> handleServiceErrors(ServiceException ex) {
                log.error(String.format("service error occured while executing request: %s", ex.getErrorMessage()), ex);
                return new ResponseEntity<>(
                                ErrorResponse.builder().errorCode(ex.getErrorCode())
                                                .errorMessage(ex.getErrorMessage()).build(),
                                ex.getHttpStatus());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
                String err = ex.getBindingResult().getFieldErrors()
                                .stream().map(FieldError::getDefaultMessage).findFirst().orElse("unkown");
                log.error(String.format("validation error occured while executing request: %s", err),
                                ex);
                return new ResponseEntity<>(
                                ErrorResponse.builder().errorCode(HttpStatus.BAD_REQUEST.value()).errorMessage(err)
                                                .build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentErrors(IllegalArgumentException ex) {
                log.error(String.format("illegal argument error occured while executing request: %s", ex.getMessage()),
                                ex);
                return new ResponseEntity<>(
                                ErrorResponse.builder().errorCode(HttpStatus.BAD_REQUEST.value())
                                                .errorMessage(ex.getMessage()).build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Throwable.class)
        public ResponseEntity<ErrorResponse> handleServerErrors(Exception ex) {
                log.error(String.format("unhandled error occured while executing request: %s", ex.getMessage()),
                                ex);
                return new ResponseEntity<>(
                                ErrorResponse.builder().errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .errorMessage(String.format("unexpected error occured: %s",
                                                                ex.getMessage()))
                                                .build(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
