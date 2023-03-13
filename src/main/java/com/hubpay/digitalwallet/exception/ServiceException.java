package com.hubpay.digitalwallet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public ServiceException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorMessage = message;
    }

    public ServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = httpStatus.value();
        this.httpStatus = httpStatus;
        this.errorMessage = message;
    }

}
