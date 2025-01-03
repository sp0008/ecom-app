package com.app.ecom.ecommerce_app.exception;

public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String message) {
		super(message);
	}
	
}
