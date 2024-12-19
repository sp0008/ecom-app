package com.app.ecom.ecommerce_app.security;


public class SecurityConstants {

    public static final String SECRET_KEY = "your-secret-key"; // Change to a stronger secret key in production
    public static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}