package com.three4clavin.proxy.broadcast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    final String message;

    public BadRequestException (String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
