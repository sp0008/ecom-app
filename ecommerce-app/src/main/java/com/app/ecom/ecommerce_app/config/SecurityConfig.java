package com.app.ecom.ecommerce_app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.AuthorizeRequestsDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.ecom.ecommerce_app.security.JwtAuthenticationEntryPoint;
import com.app.ecom.ecommerce_app.security.JwtAuthenticationFilter;
import com.app.ecom.ecommerce_app.service.UserServices;


@Configuration
@EnableWebSecurity(debug=true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{


	@Autowired
	 DataSource dataSource;
	 
	@Autowired
	 private JwtAuthenticationEntryPoint unauthorizedHandler;
	
	  @Autowired
	private UserServices userServices;
	  
	@Bean
	public JwtAuthenticationFilter authenticationJwtTokenFilter() {
		return new JwtAuthenticationFilter();
	}
	
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)throws Exception{
		http.authorizeHttpRequests(authorizeRequests -> 
		    authorizeRequests.requestMatchers("/h2-console/**").permitAll()
		    .requestMatchers("/api/users/**").permitAll()
		    .anyRequest().authenticated());
	  
		http.sessionManagement(
				session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		
		http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
		
		http.headers(headers-> headers
				.frameOptions(frameOptions -> frameOptions.sameOrigin())
				);
		
		http.csrf(csrf->csrf.disable());
		
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception{
		return builder.getAuthenticationManager();
	}
	    
}
