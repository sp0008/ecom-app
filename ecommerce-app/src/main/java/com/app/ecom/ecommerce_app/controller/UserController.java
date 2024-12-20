package com.app.ecom.ecommerce_app.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.ecom.ecommerce_app.exception.DatabaseAccessException;
import com.app.ecom.ecommerce_app.exception.DatabaseConstraintViolationException;
import com.app.ecom.ecommerce_app.exception.IncorrectPasswordException;
import com.app.ecom.ecommerce_app.exception.InvalidArgumentException;
import com.app.ecom.ecommerce_app.exception.ProductNotFoundException;
import com.app.ecom.ecommerce_app.exception.UserAlreadyExistsException;
import com.app.ecom.ecommerce_app.exception.UserNotFoundException;
//import com.app.ecom.ecommerce_app.exception.RewardNotFoundException;
//import com.app.ecom.ecommerce_app.exception.UserNotFoundException;
import com.app.ecom.ecommerce_app.model.Address;
import com.app.ecom.ecommerce_app.model.Product;
import com.app.ecom.ecommerce_app.model.Role;
import com.app.ecom.ecommerce_app.model.User;
import com.app.ecom.ecommerce_app.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService=userService;
	}
	
	//register new user
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user){
		 try {
	            userService.registerUser(user);
	            return ResponseEntity.ok("User created successfully");
	        } catch (InvalidArgumentException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
	        } catch (UserAlreadyExistsException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
	        } catch (DatabaseConstraintViolationException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // 400 Bad Request
	        } catch (DatabaseAccessException e) {
	            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
	        } catch (Exception e) {
	            return new ResponseEntity<>("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
	        }
	}
	
	//fetch user by Id
	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId) {
	    try {
	        User user = userService.findUserById(userId);
	        return ResponseEntity.ok(user);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (InvalidArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    } catch (Exception e) {
	        // Handle any other unexpected exceptions
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	//fetch all users
	@GetMapping
	public ResponseEntity<?> getAllUsers() {
	    try {
	        List<User> users = userService.findAllUsers();
	        return ResponseEntity.ok(users);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    } catch (Exception e) {
	        // Handle any unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("An unexpected error occurred: " + e.getMessage());
	    }
	}

	//update user details
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUserDetails(@PathVariable Long userId, @RequestBody User updatedUser) {
	    try {
	        User user = userService.updateUserDetails(userId, updatedUser);
	        return ResponseEntity.ok(user);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    } catch (DatabaseConstraintViolationException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	    } catch (InvalidArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);	                             
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);	                            
	    } catch (Exception e) {
	        // Catch any other unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);	                            
	    }
	}

	
	//delete user by using user Id
	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
	    try {
	        userService.deleteUser(userId);
	        return ResponseEntity.ok("User deleted successfully");
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (DatabaseConstraintViolationException e) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
	    } catch (InvalidArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    } catch (RuntimeException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred while deleting the user.");
	    }
	}

	
	//add user address
	@PostMapping("/{userId}/addresses")
	public ResponseEntity<String> addAddress(@PathVariable Long userId, @RequestBody Address address) {
	    try {
	        userService.addAddress(userId, address);
	        return ResponseEntity.ok("Address added successfully for user ID " + userId);
	    } catch (InvalidArgumentException e) {
	        return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred: " + e.getMessage());
	    }
	}

	
	//add product to user wishlist
	@PostMapping("/{userId}/wishlist/{productId}")
	public ResponseEntity<String> addWishlist(@PathVariable Long userId, @PathVariable Long productId) {
	    try {
	        userService.addToWishlist(userId, productId);
	        return ResponseEntity.ok("Product with ID " + productId + " added to wishlist of user with ID " + userId);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
	    } catch (ProductNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
	    }
	}

	
	//fetch products from user wishlist
	@GetMapping("/{userId}/wishlist")
	public ResponseEntity<List<Product>> getUserWishlist(@PathVariable Long userId) {
	    try {
	        // Fetch the wishlist of the user, throw an exception if user not found
	        List<Product> wishlist = userService.getUserWishlist(userId);
	        
	        return ResponseEntity.ok(wishlist);
	    } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
	    } catch (Exception e) {
	        // Handle any other unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.emptyList()); // Optionally return an empty list on error
	    }
	}

	
	//add products to user's order history
	@PostMapping("{userId}/order-history/{productId}")
	public ResponseEntity<String> addToOrderHistory(@PathVariable Long userId, @PathVariable Long productId) {
	    try {
	        // Call the service method to add the product to the user's order history
	        userService.addToOrderHistory(userId, productId);

	        return ResponseEntity.ok("Product with Id " + productId + " added to Order History of user with ID " + userId);
	    } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found.");
	    } catch (ProductNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product with ID " + productId + " not found.");
	    } catch (Exception e) {
	        // Handle unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding to order history: " + e.getMessage());
	    }
	}

		
	//get order history of a user
	@GetMapping("/{userId}/order-history")
	public ResponseEntity<List<Product>> getUserOrderHistory(@PathVariable Long userId) {
	    try {
	        // Attempt to retrieve the user's order history
	        List<Product> orderHistory = userService.getOrderHistory(userId);
	        if (orderHistory.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(orderHistory);
	        }
	        
	        return ResponseEntity.ok(orderHistory);

	    } catch (UserNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception ex) {
	        // Handle any other unexpected exceptions
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null);
	    }
	}

	
	//add products to user cart
	@PostMapping("/{userId}/cart/{productId}")
	public ResponseEntity<String> addToCart(@PathVariable Long userId, @PathVariable Long productId) {
	    try {
	        userService.addToCart(userId, productId);
	        return ResponseEntity.ok("Product with ID " + productId + " added to the cart of user with ID " + userId);
	    } catch (UserNotFoundException e) {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
	    } catch (ProductNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
	    } catch (IllegalArgumentException e) {
	         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input: " + e.getMessage());
	    } catch (Exception e) {
	        // Handle any unexpected exception and return 500 Internal Server Error
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
	    }
	}	
	
	//fetch products from user cart
	@GetMapping("/{userId}/cart")
	public ResponseEntity<List<Product>> getUserCart(@PathVariable Long userId) {
	    try {
	        List<Product> cart = userService.getUserCart(userId);

	        return ResponseEntity.ok(cart);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception e) {
	    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(null); 
	    }
	}


	//deactivate user account
	@PatchMapping("/{userId}/deactivate")
	public ResponseEntity<String> deactivateUserAccount(@PathVariable Long userId) {
	    try {
	        userService.deactivateUserAccount(userId);
	        
	        return ResponseEntity.ok("User account with Id " + userId + " is deactivated successfully");
	        
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	    } catch (DatabaseConstraintViolationException e) {
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	    } catch (Exception e) {
	        // Handle any other unexpected errors
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while deactivating the user account.");
	    }
	}

	// Change user role
	@PutMapping("/{userId}/change-role")
	public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestBody Role newRole) {
	    try {
	        userService.assignUserRole(userId, newRole);
	        return ResponseEntity.ok("Role is changed for user with ID " + userId);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID " + userId);
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error accessing the database");
	    }
	}

	// Change password using user Id
	@PatchMapping("/{id}/change-pwd")
	public ResponseEntity<String> updatePwd(@PathVariable Long id, 
	                        @RequestParam String oldPwd, 
	                       @RequestParam String newPwd) {
	    try {
	        userService.changePwd(id, oldPwd, newPwd);
	        return ResponseEntity.ok("Password changed successfully");
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID " + id);
	    } catch (IncorrectPasswordException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error accessing the database");
	    }
	}

	// Checking if email is available
	@GetMapping("/check-mail")
	public ResponseEntity<Boolean> checkMail(@RequestParam String email) {
	    try {
	        boolean isAvailable = userService.isEmailAvailable(email);
	        return ResponseEntity.ok(isAvailable);
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
	    }
	}

	// Checking if a username is available
	@GetMapping("/check-username")
	public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
	    try {
	        boolean isAvailable = userService.userNameAvailable(username);
	        return ResponseEntity.ok(isAvailable);
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
	    }
	}

	// Update profile image URL for user
	@PatchMapping("/{userId}/update-profile-image")
	public ResponseEntity<String> updateUserProfileImage(@PathVariable Long userId, @RequestParam String profileImageUrl) {
	    try {
	        userService.updateProfileImage(userId, profileImageUrl);
	        return ResponseEntity.ok("Profile Image URL updated for user with ID " + userId);
	    } catch (UserNotFoundException e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with ID " + userId);
	    } catch (DatabaseAccessException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error accessing the database");
	    }
	}

	
	//fetch user rewards
	@GetMapping("/{userId}/rewards")
	public ResponseEntity<Long> fetchUserRewards(@PathVariable Long userId) {
	    try {
	        Optional<Long> rewards = userService.getUserReward(userId);
	        
	        // Check if the reward is present and greater than or equal to 0
	        if (rewards.isPresent() && rewards.get() >= 0) {
	            return ResponseEntity.ok(rewards.get());
	        } else {
	            // In case of invalid rewards (optional, depending on your logic)
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0L);
	        }
	    } catch (RuntimeException ex) {
	        // Handling user not found exception
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    } catch (Exception ex) {
	        // Handling reward not found exception (if needed)
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
	}


	
}
