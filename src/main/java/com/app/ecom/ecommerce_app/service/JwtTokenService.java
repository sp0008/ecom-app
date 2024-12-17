package com.app.ecom.ecommerce_app.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.app.ecom.ecommerce_app.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtTokenService {
//    private final String SECRET_KEY = "secret_key";  // Use a strong secret key
//    private final long EXPIRATION_TIME = 86400000L; // 24 hours in milliseconds
//
//    public String generateToken(User user) {
//        return Jwts.builder()
//                .setSubject(user.getUsername())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .claim("role", user.getRole())  // Include user role (or other claims)
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    public Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(SECRET_KEY)
//                .parseBuilder(token)
//                .getBody();
//    }
//
//    public String extractUsername(String token) {
//        return extractClaims(token).getSubject();
//    }
//
//    public boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//
//    public boolean validateToken(String token, User user) {
//        String username = extractUsername(token);
//        return username.equals(user.getUsername()) && !isTokenExpired(token);
//    }
}
