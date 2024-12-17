package com.app.ecom.ecommerce_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Address {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String street;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
}
