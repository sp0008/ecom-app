package com.app.ecom.ecommerce_app.exception;

public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(String message) {
		super(message);
	}
}
