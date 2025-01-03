package com.app.ecom.ecommerce_app.security;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
		@Autowired
	  private JwtUtil jwtUtils;
	  
		@Autowired
	  private UserDetailsService userDetailsService;

	@Override
	 protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	throws IOException, ServletException{
		
		
		log.trace("Authentication called for URI: {}", request);
		System.out.println(request.getRequestURI());
		
		try {	
			//setting the signin api to be publicly accessible
			if ("/api/users/signin".equals(request.getRequestURI()) || ("/api/users/register".equals(request.getRequestURI()))) {
 	            filterChain.doFilter(request, response);
	            return;
	        }
			
			System.out.println("print");

			
			String jwt=parseJwt(request);
			if(jwt!=null && jwtUtils.validateJwtToken(jwt) ) {
				String username=jwtUtils.getUserNameFromJwtToken(jwt);
				UserDetails userDetails=userDetailsService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken( userDetails, null, userDetails.getAuthorities());
				log.debug("Roles from JWT: {}", userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}	
			}catch (Exception e) {
				log.error("Cannot set user");
			}
			
			filterChain.doFilter(request, response);
		}
		
	
	
	private String parseJwt(HttpServletRequest request) {
		String jwt=jwtUtils.getjwtFromHeader(request);
		log.debug("AuthTokenFilter.java:{}", jwt);
		return jwt;
	}
	
}