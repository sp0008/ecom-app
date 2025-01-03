package com.app.ecom.ecommerce_app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity //mapped with user table in db
@Data   
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true, nullable=false)
    @jakarta.validation.constraints.NotBlank(message = "Username is required")
	private String username;
	
	@Column(nullable=false)
	@jakarta.validation.constraints.NotBlank(message = "Password is required")
	private String password;
	
	@Column(nullable=false, unique=true)
	@jakarta.validation.constraints.Email(message = "Email should be valid")
	private String email;
	
	@Column(nullable = true, unique = true)
	@Pattern(regexp = "\\d{10,15}", message = "Phone number should be between 10 and 15 digits")
    private String phoneNumber;
	
    @Column(unique = true)
    @Email(message = "Alternate email should be valid")
    private String alternateEmail;

    @NotBlank(message = "First name is required")
    private String firstName;

    @Column
    @NotBlank(message="Last name is required")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Column(nullable=true)
    private String profileImageUrl;

     @Column(nullable=false, updatable=false)
    private LocalDate registrationDate;

     @Column(nullable=false)
    private LocalDateTime lastLoginDate;

     @Column(nullable=false)
    private Boolean accountStatus;

     //one to one can be stored as json
     //use Id to store, rather than entire object
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> wishList;

    //separate table for user id and orders(order id)
    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> orderHistory;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Product> cart;

    @Enumerated(EnumType.STRING)
    private Language preferredLanguage;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Long rewardPoints;

    @Enumerated(EnumType.STRING)
    private MembershipLevel membershipLevel;
	
	@Enumerated(EnumType.STRING)
	private Role role;

	 
	 //JPA calls this method before saving entity to database
	 @PrePersist
	 public void onPrePersist() {
		 if(registrationDate==null) {
			 registrationDate=LocalDate.now();
		 }
         lastLoginDate=LocalDateTime.now();
		 System.out.println("Before persisting :  Setting registration date");
		 
	 }
	 
	 //JPA calls this method before updating entity to database
	 @PreUpdate
	 public void onPreUpdate() {
		 lastLoginDate=LocalDateTime.now();
		 System.out.println("Before Updating: Setting the last login date and time");
	 }
	 
}
