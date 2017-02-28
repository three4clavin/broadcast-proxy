package com.three4clavin.proxy.broadcast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_GATEWAY)
public class ProxyException extends RuntimeException {
    final String message;

    public ProxyException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
