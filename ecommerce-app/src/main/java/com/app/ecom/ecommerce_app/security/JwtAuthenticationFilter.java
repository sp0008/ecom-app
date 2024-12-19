package com.app.ecom.ecommerce_app.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.ecom.ecommerce_app.config.JwtUtil;
import com.app.ecom.ecommerce_app.service.UserService;
import com.app.ecom.ecommerce_app.service.UserServices;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
    private final UserServices userService;
    private final JwtUtil jwtUtil;
    
    public JwtAuthenticationFilter(UserServices userService) {
    	this.userService = userService;
    	this.jwtUtil=null;
    }

    public JwtAuthenticationFilter(UserServices userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get the token from the Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // If the header exists and starts with "Bearer"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            String jwtToken = authorizationHeader.substring(7); // Extract the token (without "Bearer ")

            try {
                // Validate the token
                if (jwtUtil.validateToken(jwtToken)) {
                    String username = jwtUtil.extractUsername(jwtToken);

                    // Fetch user details using custom user service
                    var userDetails = userService.loadUserByUsername(username);

                    // Create an authentication token and set it to the context
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                
            	 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 response.getWriter().write("Invalid or expired JWT token.");
                 return; 
            }
        }

        // Continue the request-response chain
        filterChain.doFilter(request, response);
    }
}