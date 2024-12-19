package com.app.ecom.ecommerce_app.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "your-secret-key"; // Keep it secure

    // Generate JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())  // Use byte[] for the key
                .compact();
    }

    // Validate JWT token
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    // Extract username from JWT token
    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Check if the token has expired
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    // Extract expiration date from token
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    // Extract Claims from the token
 // Extract Claims from the token
    private Claims getClaimsFromToken(String token) {
        try {
            byte[] key = null;
			JwtParser jwtParser = Jwts.parser() // Use the new parserBuilder() method
                    .setSigningKey(key)  // Set the signing key
                    .build();  // Build the JwtParser

            return jwtParser.parseClaimsJws(token).getBody();  // Parse and extract claims
        } catch (JwtException e) {
            // Log or handle the exception as needed
            System.out.println("Invalid JWT: " + e.getMessage());
            return null;
        }
    }


}

