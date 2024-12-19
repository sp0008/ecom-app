package com.app.ecom.ecommerce_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.ecom.ecommerce_app.security.JwtAuthenticationEntryPoint;
import com.app.ecom.ecommerce_app.security.JwtAuthenticationFilter;
import com.app.ecom.ecommerce_app.service.UserServices;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final UserServices userService; // Inject UserService to provide custom user details

    // Constructor injection of JwtAuthenticationEntryPoint and UserService
    public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler, UserServices userService) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userService = userService;
    }

    // Define the security filter chain for Spring Security 6
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()  // Use 'authorizeHttpRequests' for Spring Security 6
            .requestMatchers(HttpMethod.GET, "/api/users/check-mail", "/api/users/check-username").permitAll()
            .requestMatchers("/api/users/register").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users/**/cart", "/api/users/**/wishlist", "/api/users/**/order-history")
                .hasAnyRole("CUSTOMER", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/users/**/addresses", "/api/users/**/wishlist", "/api/users/**/order-history")
                .hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.PATCH, "/api/users/**/deactivate", "/api/users/**/change-pwd", "/api/users/**/update-profile-image")
                .hasRole("CUSTOMER")
            .requestMatchers(HttpMethod.PUT, "/api/users/**/change-role").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userService; // Use your custom UserService to load user details
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Define the password encoder for password hashing
    }
    
//    @Bean
//    public AuthenticationManagerBuilder authenticationManagerBuilder(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
//        auth.userDetailsService(userService);  // Use your custom UserDetailsService
//        return auth;
//    }
    
}
