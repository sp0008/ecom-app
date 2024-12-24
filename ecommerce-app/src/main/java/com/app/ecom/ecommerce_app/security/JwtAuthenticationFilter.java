package com.app.ecom.ecommerce_app.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.ecom.ecommerce_app.service.UserService;
import com.app.ecom.ecommerce_app.service.UserServices;
import com.app.ecom.ecommerce_app.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
		@Autowired
	  private JwtUtil jwtUtils;
	  
		@Autowired
	  private UserDetailsService userDetailsService;
	    
	 private static final Logger logger=LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	
	
	@Override
	 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	throws IOException, ServletException{
		
		
		logger.error("Authentication called for URI: {}", request);
		System.out.println(request.getRequestURI());
		
		try {	
			//setting the signin api to be publicly accessible
			if ("/api/users/signin".equals(request.getRequestURI())) {
	            filterChain.doFilter(request, response);
	            return;
	        }
			
			System.out.println("print");

			
			String jwt=parseJwt(request);
			if(jwt!=null && jwtUtils.validateJwtToken(jwt) ) {
				String username=jwtUtils.getUserNameFromJwtToken(jwt);
				UserDetails userDetails=userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities());
				logger.debug("Roles from JWT: {}", userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}	
			}catch (Exception e) {
				logger.error("Cannot set user");
			}
			
			filterChain.doFilter(request, response);
		}
		
	
	
	private String parseJwt(HttpServletRequest request) {
		String jwt=jwtUtils.getjwtFromHeader(request);
		logger.debug("AuthTokenFilter.java:{}", jwt);
		return jwt;
	}
	
}