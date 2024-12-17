package com.app.ecom.ecommerce_app.exception;

public class DatabaseAccessException extends RuntimeException {

    public DatabaseAccessException(String message) {
        super(message);
    }
}