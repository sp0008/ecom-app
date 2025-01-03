package com.app.ecom.ecommerce_app.exception;

public class DatabaseConstraintViolationException extends RuntimeException {

    public DatabaseConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}