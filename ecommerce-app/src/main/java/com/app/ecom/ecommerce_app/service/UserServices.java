package com.app.ecom.ecommerce_app.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.app.ecom.ecommerce_app.repository.UserRepository;

@Service
public class UserServices implements UserDetailsService {

    private final UserRepository userRepository;

    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String role = user.getRole().toString();  // Assuming user.getRole() returns a Role enum or custom class

        // Convert role to SimpleGrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        
        // Map the user to UserDetails (Spring Security User)
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // password should be already hashed
                .authorities(Collections.singletonList(authority)) // Set authorities
                .build();
    }
}
