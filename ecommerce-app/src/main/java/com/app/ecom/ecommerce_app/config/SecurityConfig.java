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
	private UserServices userServices; // Make sure this is injected correctly
	
	@Bean
	public JwtAuthenticationFilter authenticationJwtTokenFilter() {
		return new JwtAuthenticationFilter();
	}
	
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)throws Exception{
		http.authorizeHttpRequests(authorizeRequests -> 
		    authorizeRequests.requestMatchers("/h2-console/**").permitAll()
		    .requestMatchers("/api/users/**").permitAll()
			.requestMatchers("/error").permitAll()
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
	
	
//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception{
		return builder.getAuthenticationManager();
	}
	
	
//	@Bean
//	public UserDetailsService userdetailsService(UserServices userServices) {
//		return userServices;
//	}

    // Define the security filter chain for Spring Security 6
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .authorizeRequests()  // Use 'authorizeHttpRequests' for Spring Security 6
//            .requestMatchers(HttpMethod.GET, "/api/users/check-mail", "/api/users/check-username").permitAll()
//            .requestMatchers("/api/users/register").permitAll()
//            .requestMatchers(HttpMethod.GET, "/api/users/**/cart", "/api/users/**/wishlist", "/api/users/**/order-history")
//                .hasAnyRole("CUSTOMER", "ADMIN")
//            .requestMatchers(HttpMethod.POST, "/api/users/**/addresses", "/api/users/**/wishlist", "/api/users/**/order-history")
//                .hasRole("CUSTOMER")
//            .requestMatchers(HttpMethod.PATCH, "/api/users/**/deactivate", "/api/users/**/change-pwd", "/api/users/**/update-profile-image")
//                .hasRole("CUSTOMER")
//            .requestMatchers(HttpMethod.PUT, "/api/users/**/change-role").hasRole("ADMIN")
//            .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
//            .anyRequest().authenticated()
//            .and()
//            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
//            .and()
//            .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//   


    
}
