package com.app.ecom.ecommerce_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.ecommerce_app.model.User;
import com.app.ecom.ecommerce_app.service.JwtTokenService;
import com.app.ecom.ecommerce_app.service.UserService;

@RestController
public class AuthController {

//    private final UserService userService;
//    private final JwtTokenService jwtTokenService;
//
//    public AuthController(UserService userService, JwtTokenService jwtTokenService) {
//        this.userService = userService;
//        this.jwtTokenService = jwtTokenService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequest loginRequest) {
//        User user = userService.findByUsername(loginRequest.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (!userService.checkPassword(loginRequest.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Invalid password");
//        }
//
//        String jwtToken = jwtTokenService.generateToken(user);
//        return ResponseEntity.ok(jwtToken);
//    }
}
