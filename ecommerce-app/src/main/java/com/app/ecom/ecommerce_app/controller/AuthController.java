package com.app.ecom.ecommerce_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.ecommerce_app.config.JwtUtil;
import com.app.ecom.ecommerce_app.dto.AuthResponse;
import com.app.ecom.ecommerce_app.dto.LoginRequest;
import com.app.ecom.ecommerce_app.model.User;
import com.app.ecom.ecommerce_app.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            // Generate JWT token
            String token = jwtUtil.generateToken(loginRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            // Handle invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during authentication");
        }
    }

//    @GetMapping("/protected-endpoint")
//    public ResponseEntity<?> accessProtectedEndpoint(@RequestHeader("Authorization") String authorizationHeader) {
//        // Validate token and extract username
//        String token = authorizationHeader.replace("Bearer ", "");
//        if (!jwtUtil.validateToken(token)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//        }
//
//        String username = jwtUtil.extractUsername(token);
//        return ResponseEntity.ok("Hello, " + username);
//    }
}
