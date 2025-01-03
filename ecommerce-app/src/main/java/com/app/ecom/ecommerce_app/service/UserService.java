package com.app.ecom.ecommerce_app.service;

import java.lang.System.Logger;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.ecom.ecommerce_app.exception.DatabaseAccessException;
import com.app.ecom.ecommerce_app.exception.DatabaseConstraintViolationException;
import com.app.ecom.ecommerce_app.exception.InvalidArgumentException;
import com.app.ecom.ecommerce_app.exception.ProductNotFoundException;
import com.app.ecom.ecommerce_app.exception.RewardNotFoundException;
import com.app.ecom.ecommerce_app.exception.UnauthorizedAccessException;
import com.app.ecom.ecommerce_app.exception.UserAlreadyExistsException;
import com.app.ecom.ecommerce_app.exception.UserNotFoundException;
//import com.app.ecom.ecommerce_app.exception.UserNotFoundException;
import com.app.ecom.ecommerce_app.model.Address;
import com.app.ecom.ecommerce_app.model.MembershipLevel;
import com.app.ecom.ecommerce_app.model.Product;
import com.app.ecom.ecommerce_app.model.MembershipLevel;
import com.app.ecom.ecommerce_app.model.Role;
import com.app.ecom.ecommerce_app.model.User;
import com.app.ecom.ecommerce_app.repository.AddressRepository;
import com.app.ecom.ecommerce_app.repository.ProductRepository;
import com.app.ecom.ecommerce_app.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;

@Service
public class UserService {

	
	private final UserRepository userRepo;
	private final PasswordEncoder pwdEncoder; //to encode the password
	private final AddressRepository addressRepo;
	private final ProductRepository productRepo;
	
	//private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	
	public UserService(UserRepository userRepo, PasswordEncoder pwdEncoder, AddressRepository addressRepo, ProductRepository productRepo) {
		this.userRepo=userRepo;
		this.pwdEncoder=pwdEncoder;
		this.addressRepo=addressRepo;
		this.productRepo=productRepo;
	}
	

	//register new user
	public User registerUser(User user) {

    if (user.getUsername() == null || user.getUsername().isBlank()) {
        throw new InvalidArgumentException("Username is required");
    }
    if (user.getPassword() == null || user.getPassword().isBlank()) {
        throw new InvalidArgumentException("Password is required");
    }
    if (user.getEmail() == null || !user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
        throw new InvalidArgumentException("Valid email is required");
    }
    
    try {
        user.setPassword(pwdEncoder.encode(user.getPassword()));
        user.setAccountStatus(true);

        return userRepo.save(user);
    } catch (DataIntegrityViolationException e) {
        throw new UserAlreadyExistsException("A user with this username or email already exists.");
    } catch (ConstraintViolationException e) {
        throw new DatabaseConstraintViolationException("Database constraint violation: invalid data format or uniqueness violation.", e);
    } catch (DataAccessException e) {
        throw new DatabaseAccessException("Database access error occurred while saving the user.");
    }
   }

	// find user using user name
	public Optional<User> findByUsername(String username) {
	    try {
	        if (username == null || username.isBlank()) {
	            throw new InvalidArgumentException("Username cannot be null or blank");
	        }
	        return userRepo.findByUsername(username);
	    } catch (InvalidArgumentException e) {        
	        throw new InvalidArgumentException("Invalid username: " + e.getMessage());
	    } catch (EmptyResultDataAccessException e) {
	    	throw new UserNotFoundException("User not found for username: " + username);
	    } catch (DataAccessException e) {        
	        throw new DatabaseAccessException("Error accessing the database while finding the user");
	    } catch (Exception e) {
	        // Handle any other unexpected errors
	        throw new RuntimeException("Unexpected error occurred while retrieving the user", e);
	    }
	}
	
	
	//update user details for a specific user
	public User updateUserDetails(Long userId, User updatedUserDetails) {
	    try {
	        User existingUser = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
	        
	        if (updatedUserDetails.getUsername() != null) {
	            existingUser.setUsername(updatedUserDetails.getUsername());
	        }
	        if (updatedUserDetails.getEmail() != null) {
	            existingUser.setEmail(updatedUserDetails.getEmail());
	        }
	        if (updatedUserDetails.getPhoneNumber() != null) {
	            existingUser.setPhoneNumber(updatedUserDetails.getPhoneNumber());
	        }
	        if (updatedUserDetails.getAlternateEmail() != null) {
	            existingUser.setAlternateEmail(updatedUserDetails.getAlternateEmail());
	        }
	        if (updatedUserDetails.getFirstName() != null) {
	            existingUser.setFirstName(updatedUserDetails.getFirstName());
	        }
	        if (updatedUserDetails.getLastName() != null) {
	            existingUser.setLastName(updatedUserDetails.getLastName());
	        }
	        if (updatedUserDetails.getDateOfBirth() != null) {
	            existingUser.setDateOfBirth(updatedUserDetails.getDateOfBirth());
	        }
	        if (updatedUserDetails.getPreferredLanguage() != null) {
	            existingUser.setPreferredLanguage(updatedUserDetails.getPreferredLanguage());
	        }
	        if (updatedUserDetails.getProfileImageUrl() != null) {
	            existingUser.setProfileImageUrl(updatedUserDetails.getProfileImageUrl());
	        }
	        if (updatedUserDetails.getCurrency() != null) {
	            existingUser.setCurrency(updatedUserDetails.getCurrency());
	        }

	        // Handle membership level changes
	        if (updatedUserDetails.getMembershipLevel() != null) {
	            if (isMembershipChangeAllowed(existingUser, updatedUserDetails.getMembershipLevel())) {
	                existingUser.setMembershipLevel(updatedUserDetails.getMembershipLevel());
	            } else {
	                throw new IllegalArgumentException("Membership level change not allowed.");
	            }
	        }

	        // Save the updated user
	        return userRepo.save(existingUser);
	        
	    } catch (DataIntegrityViolationException e) {
	        throw new DatabaseAccessException("Database integrity violation while updating user details.");
	    } catch (ConstraintViolationException e) {
	        throw new DatabaseConstraintViolationException("Database constraint violation: invalid data format or uniqueness violation.", e);
	    } catch (IllegalArgumentException e) {
	        throw new InvalidArgumentException("Invalid argument: " + e.getMessage());
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error accessing database while updating user details.");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while updating user details.", e);
	    }
	}
	
	// Method to check if membership change is allowed
	private boolean isMembershipChangeAllowed(User existingUser, MembershipLevel newMembershipLevel) {
	    try {
	        if (existingUser == null) {
	            throw new InvalidArgumentException("Existing user cannot be null.");
	        }
	        if (newMembershipLevel == null) {
	            throw new InvalidArgumentException("New membership level cannot be null.");
	        }

	        MembershipLevel currentMembershipLevel = existingUser.getMembershipLevel();

	        if (currentMembershipLevel == null) {
	            throw new IllegalStateException("Current membership level is not set for the user.");
	        }
	        
	        if (currentMembershipLevel == MembershipLevel.PREMIUM && 
	            (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.FREE)) {
	            return true; 
	        }

	        if (currentMembershipLevel == MembershipLevel.PRO && 
	            (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.FREE)) {
	            return true; 
	        }

	        if (currentMembershipLevel == MembershipLevel.BASIC && newMembershipLevel == MembershipLevel.FREE) {
	            return true; 
	        }

	        if (currentMembershipLevel == MembershipLevel.BASIC && 
	            (newMembershipLevel == MembershipLevel.PRO || newMembershipLevel == MembershipLevel.PREMIUM)) {
	            return true; 
	        }

	        if (currentMembershipLevel == MembershipLevel.PRO && newMembershipLevel == MembershipLevel.PREMIUM) {
	            return true; 
	        }

	        // Allow upgrade to "Pro" from "Free"
	        if (currentMembershipLevel == MembershipLevel.FREE && newMembershipLevel == MembershipLevel.PRO) {
	            return true;
	        }

	        // Allow upgrade to "Basic" or "Pro" from "Free"
	        if (currentMembershipLevel == MembershipLevel.FREE && 
	            (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.PRO)) {
	            return true;
	        }

	        // Allow upgrade to "Premium" from "Basic" or "Pro"
	        if ((currentMembershipLevel == MembershipLevel.BASIC || currentMembershipLevel == MembershipLevel.PRO) && 
	            newMembershipLevel == MembershipLevel.PREMIUM) {
	            return true; 
	        }

	        // If no valid transition is found, return false
	        return false;

	    } catch (InvalidArgumentException | IllegalStateException e) {
	        throw e; // Re-throw expected exceptions
	    } catch (Exception e) {
	        // Re-throw unexpected errors as a runtime exception
	        throw new RuntimeException("Unexpected error occurred while validating membership change", e);
	    }
	}



	//delete user by using user Id
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteUser(Long userId) {
	    try {
	        
	        if (!userRepo.existsById(userId)) {
	            throw new UserNotFoundException("User not found with ID: " + userId);
	        }

	        // Attempt to delete the user
	        userRepo.deleteById(userId);

	    } catch (UserNotFoundException e) {
	        // Re-throw user not found exception (already a custom exception)
	        throw e;
	    } catch (DataIntegrityViolationException e) {
	        // Handle cases where deletion violates database integrity (e.g., foreign key constraints)
	        throw new DatabaseConstraintViolationException(
	            "Cannot delete user with ID " + userId + ". There are dependent records in the database.", e);
	    } catch (DataAccessException e) {
	        // Handle general database access issues
	        throw new DatabaseAccessException("Database error occurred while attempting to delete the user with ID: " + userId);
	    } catch (IllegalArgumentException e) {
	        // Handle invalid argument passed to the repository method (e.g., null ID)
	        throw new InvalidArgumentException("Invalid user ID provided for deletion: " + userId);
	    } catch (Exception e) {
	        // Handle all other unexpected exceptions
	        throw new RuntimeException("Unexpected error occurred while deleting the user with ID: " + userId, e);
	    }
	}

	
	//find user using user email ID
	public Optional<User> findByEmail(String email) {
	    try {
	        if (email == null || email.isBlank()) {
	            throw new InvalidArgumentException("Email must not be null or empty.");
	        }
	        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	            throw new InvalidArgumentException("Invalid email format.");
	        }
	       
	        return userRepo.findByEmail(email);
	    } catch (InvalidArgumentException e) {
	        throw e; // Rethrow for the controller to handle
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Database error occurred while finding the user by email.");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while finding the user by email.", e);
	    }
	}


	//change password of the user
	//@RateLimit(maxRequests = 5, duration = 60)
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public void changePwd(Long userId, String oldPwd, String newPwd, Authentication authentication) {
	    String authenticatedUserName = authentication.getName();
	    
	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	    if (!authenticatedUserName.equals(user.getUsername())) {
	        throw new UnauthorizedAccessException("You are not authorized to perform this action");
	    }

	    try {
	        if (!pwdEncoder.matches(oldPwd, user.getPassword())) {
	            throw new InvalidArgumentException("Old password is not correct");
	        }

	        user.setPassword(pwdEncoder.encode(newPwd));
	        userRepo.save(user);
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while updating the user's password.");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while changing the user's password", e);
	    }
	}


	
	//add address for a user
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public void addAddress(Long userId, Address address) {
	    try {
	        if (userId == null) {
	            throw new InvalidArgumentException("User ID cannot be null.");
	        }
	        if (address == null) {
	            throw new InvalidArgumentException("Address cannot be null.");
	        }
	        
	        User user = userRepo.findById(userId)
	                            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

	        // Set user to address and save
	        address.setUser(user);
	        addressRepo.save(address);
	    } catch (InvalidArgumentException e) {
	        throw e; // Rethrow to be handled in the controller
	    } catch (UserNotFoundException e) {
	        throw e; // Rethrow for the controller to handle user not found exception
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Database error occurred while adding the address.");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while adding the address.", e);
	    }
	}

	
	//add product to user wishlist
	public void addToWishlist(Long userId, Long productId) {
	    try {
	        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));
	        Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with ID " + productId));
	        
	        user.getWishList().add(product);
	        userRepo.save(user);
	    } catch (UserNotFoundException e) {
	        throw e; // Rethrow custom exception
	    } catch (ProductNotFoundException e) {
	        throw e; // Rethrow custom exception
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Database access error while adding product to wishlist");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while adding product to wishlist", e);
	    }
	}


	//add product to user cart
	public void addToCart(Long userId, Long productId) {
	    try {
	         User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));
	        
	         Product product = productRepo.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with ID " + productId));
	        
	        // Add product to the user's cart
	        user.getCart().add(product);
	       
	        userRepo.save(user);
	    } catch (UserNotFoundException e) {
	          throw e;  // Rethrow the exception so the controller can handle it appropriately
	    } catch (ProductNotFoundException e) {
	      	        throw e;  // Rethrow the exception so the controller can handle it appropriately
	    } catch (Exception e) {
	        // Catch any unexpected exceptions
	        throw new RuntimeException("An unexpected error occurred while adding the product to the cart.", e);
	    }
	}

	
	//add product to order history
	public void addToOrderHistory(Long userId, Long productId) {
	    try {
		        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

      	        Product product = productRepo.findById(productId)
	                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID " + productId));

		        user.getOrderHistory().add(product);

	        // Save the updated user with the new order history
	        userRepo.save(user);

	    } catch (UserNotFoundException | ProductNotFoundException e) {
	        // Rethrow the specific exception to be handled by the controller or higher-level error handler
	        throw e;
	    } catch (Exception e) {
	        // Log and throw any other unexpected exceptions as a RuntimeException
	        throw new RuntimeException("Unexpected error occurred while adding product to order history: " + e.getMessage(), e);
	    }
	}

	
	//get user wishlist
	public List<Product> getUserWishlist(Long userId) {
	    try {
	        // Fetch the user by ID and throw an exception if the user does not exist
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        // Return the user's wishlist
	        return user.getWishList();
	    } catch (UserNotFoundException e) {
	        throw new UserNotFoundException("User not found with ID " + userId);
	    } catch (Exception e) {
	        throw new RuntimeException("An unexpected error occurred while fetching the user's wishlist", e);
	    }
	}

	
	
	//get user cart
	public List<Product> getUserCart(Long userId) {
	    try {
	        // Fetch the user by ID and throw an exception if the user does not exist
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        // Return the user's cart
	        return user.getCart();
	    } catch (UserNotFoundException e) {
	        // Handle the case where the user is not found
	        throw e;  // Rethrow the custom exception
	    } catch (Exception e) {
	        // Handle any other unexpected errors
	        throw new RuntimeException("An unexpected error occurred while fetching the user's cart", e);
	    }
	}

     //get user order history
	public List<Product> getOrderHistory(Long userId) {
	    try {
	        // Attempt to find the user by ID
	        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        // Return the user's order history if found
	        return user.getOrderHistory();
	    } catch (UserNotFoundException e) {
	        throw e;  // Re-throwing the exception so it can be handled globally
	    } catch (Exception e) {
	        // Handle any unexpected exceptions
	        throw new RuntimeException("An error occurred while retrieving order history: " + e.getMessage());
	    }
	}

	
	//deactivate user account
	public void deactivateUserAccount(Long userId) {
	    try {
	        // Find the user by ID, throw a custom exception if not found
	        User user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));
	        
	        // Set account status to false to deactivate
	        user.setAccountStatus(false);
	        
	        // Save the user after deactivation
	        userRepo.save(user);
	    } catch (UserNotFoundException ex) {
	        // Handle user not found exception (404)
	        throw ex;  // Rethrow the custom exception
	    } catch (DataIntegrityViolationException e) {
	        // Handle database issues related to integrity constraints
	        throw new DatabaseConstraintViolationException("Database constraint violation while deactivating user account", e);
	    } catch (DataAccessException e) {
	        // Handle database access errors
	        throw new DatabaseAccessException("Error occurred while accessing the database to deactivate user account");
	    } catch (Exception e) {
	        // Handle any unexpected errors
	        throw new RuntimeException("Unexpected error occurred while deactivating user account", e);
	    }
	}

	
	
	//assign new role to user
	public User assignUserRole(Long userId, Role newRole) {
	    try {
	        // Find user by ID, if not found, throw UserNotFoundException
	        User user = userRepo.findById(userId)
	                            .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        user.setRole(newRole);
	        
	        return userRepo.save(user);
	        
	    } catch (UserNotFoundException e) {
	        throw e;  // Re-throw the UserNotFoundException
	    } catch (DataIntegrityViolationException e) {
	        throw new DatabaseConstraintViolationException("Database constraint violation while assigning role", e);
	    } catch (DataAccessException e) {
	    	throw new DatabaseAccessException("Database access error occurred while assigning role");
	    } catch (Exception e) {
          throw new RuntimeException("An unexpected error occurred while assigning role to user", e);
	    }
	}

	
	//list all users
	public List<User> findAllUsers() {
	    try {
	        List<User> users = userRepo.findAll();

	        if (users.isEmpty()) {
	            throw new UserNotFoundException("No users found in the database.");
	        }

	        return users;
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Failed to retrieve users from the database.");
	    } catch (Exception e) {
	        throw new RuntimeException("Unexpected error occurred while fetching users.", e);
	    }
	}

	//check if username is available
	public boolean userNameAvailable(String username) {
	    try {
	        return userRepo.findByUsername(username).isPresent();
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while checking username availability");
	    }
	}

	//check if email is available
	public boolean isEmailAvailable(String email) {
	    try {
	        return userRepo.findByEmail(email).isPresent();
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while checking email availability");
	    }
	}

	//find user using user id
	public User findUserById(Long userId) {
	    try {
	        return userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while retrieving user by ID");
	    }
	}
	
	//reset password using email
	//@RateLimit(maxRequests = 5, duration = 60)
	public void resetPwd(String email, String pwd) {
	    try {
	        User user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
	        
	        user.setPassword(pwdEncoder.encode(pwd));
	        userRepo.save(user);
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while resetting password");
	    }
	}

	//validate the credentials
	public boolean checkUserCred(String username, String pwd) {
	    try {
	        User user = userRepo.findByUsername(username)
	                .orElseThrow(() -> new UserNotFoundException("User name is invalid"));

	        return pwdEncoder.matches(pwd, user.getPassword());
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while checking user credentials");
	    }
	}

	//find users of specific role
	public List<User> getUsersByRole(Role role) {
	    try {
	        return userRepo.findAll().stream()
	                .filter(user -> user.getRole().equals(role))
	                .collect(Collectors.toList());
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while retrieving users by role");
	    }
	}

	//find users using username or email keyword
	public List<User> findUsers(String keyword) {
	    try {
	        return userRepo.findAll().stream()
	                .filter(user -> user.getUsername().contains(keyword) || user.getEmail().contains(keyword))
	                .collect(Collectors.toList());
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while searching users by keyword");
	    }
	}

	//update profile image url using user Id
	public void updateProfileImage(Long userId, String profileImageUrl) {
	    try {
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        user.setProfileImageUrl(profileImageUrl);
	        userRepo.save(user);
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while updating profile image");
	    }
	}

	//find the rewards using user Id
	public Optional<Long> getUserReward(Long userId) {
	    try {
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

	        Optional<Long> rewards = userRepo.findRewardPointsById(userId);
	        if (rewards.isEmpty()) {
	            throw new RewardNotFoundException("Rewards not found for user with ID " + userId);
	        }

	        return rewards;
	    } catch (DataAccessException e) {
	        throw new DatabaseAccessException("Error occurred while retrieving user rewards");
	    }
	}

	
	
	
}


