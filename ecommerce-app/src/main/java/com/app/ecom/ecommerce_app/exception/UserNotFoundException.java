package com.app.ecom.ecommerce_app.exception;

public class UserNotFoundException extends RuntimeException{

	public UserNotFoundException(String message) {
		super(message);
	}
	
}
