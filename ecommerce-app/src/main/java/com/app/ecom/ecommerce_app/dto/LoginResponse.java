package com.app.ecom.ecommerce_app.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

	
	private String jwtToken;
	private String username;
	private String role;
}
