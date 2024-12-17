package com.app.ecom.ecommerce_app.controller;

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
		userService.registerUser(user);
		return ResponseEntity.ok("User created successfully");
	}
	
	//fetch user by Id
	@GetMapping("/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Long userId){
		User user=userService.findUserById(userId);
		
		return ResponseEntity.ok(user);
	}
	
	//fetch all users
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> users=userService.findAllUsers();
		return ResponseEntity.ok(users);
	}
	
	//update user details
	@PutMapping("/{userId}")
	public ResponseEntity<User> updateUserdetails(@PathVariable Long userId, @RequestBody User updatedUser){
		User user=userService.updateUserDetails(userId, updatedUser);
		return ResponseEntity.ok(user);
	}
	
	
	//delete user by using user Id
	@DeleteMapping("/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId){
		userService.deleteUser(userId);
		return ResponseEntity.ok("User deleted successfully");
	}
	
	//add user address
	@PostMapping("/{userId}/addresses")
	public ResponseEntity<String> addAddress(@PathVariable Long userId, @RequestBody Address address){
		userService.addAddress(userId, address);
		return ResponseEntity.ok("Address added successfully for user ID "+userId);
	}
	
	//add product to user wishlist
	@PostMapping("/{userId}/wishlist/{productId}")
	public ResponseEntity<String> addWishlist(@PathVariable Long userId, @PathVariable Long productId){
		userService.addToWishlist(userId, productId);
		return ResponseEntity.ok("Prouct with ID "+productId+" added for wishlist of user with ID "+userId);
	}
	
	//fetch products from user wishlist
	@GetMapping("/{userId}/wishlist")
	public ResponseEntity<List<Product>> getUserWishlist(@PathVariable Long userId){
		List<Product> wishlist=userService.getUserWishlist(userId);
		return ResponseEntity.ok(wishlist);
	}
	
	//add products to user's order history
		@PostMapping("{userId}/order-history/{productId}")
		public ResponseEntity<String> addToOrderHistory(@PathVariable Long userId, @PathVariable Long productId){
			userService.addToOrderHistory(userId, productId);
			return ResponseEntity.ok("Product with Id "+productId+" added to Order History of user with ID "+userId);
		}
		
	//get order history of a user
	@GetMapping("/{userId}/order-history")
	public ResponseEntity<List<Product>> getUserOrderHistory(@PathVariable Long userId){
		List<Product> orderHistory=userService.getOrderHistory(userId);
		return ResponseEntity.ok(orderHistory);			
	}
	
	//add products to user cart
	@PostMapping("{userId}/cart/{productId}")
	public ResponseEntity<String> addToCart(@PathVariable Long userId, @PathVariable Long productId){
		userService.addToCart(userId, productId);
		return ResponseEntity.ok("Product with Id "+productId+" added to cart of user with ID "+userId);
	}
	
	
	//fetch products from user cart
	@GetMapping("/{userId}/cart")
	public ResponseEntity<List<Product>> getUserCart(@PathVariable Long userId){
			List<Product> cart=userService.getUserCart(userId);
			return ResponseEntity.ok(cart);
	}

	//deactivate user account
	@PatchMapping("/{userId}/deactivate")
	public ResponseEntity<String> deactivateUserAccount(@PathVariable Long userId){
		userService.deactivateUserAccount(userId);
		return ResponseEntity.ok("User account with Id "+userId+" is deactivated successfully");
	}
	
	//change user role
	@PutMapping("/{userId}/change-role")
	public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestBody Role newRole){
		userService.assignUserRole(userId, newRole);
		return ResponseEntity.ok("Role is changed for user with ID "+userId);
	}
	
	//change password using user Id
	@PatchMapping("/{id}/change-pwd")
	public ResponseEntity<String> updatePwd(@PathVariable Long id,
			@RequestParam String oldPwd, @RequestParam String newPwd){
		  userService.changePwd(id, oldPwd, newPwd);
		  return ResponseEntity.ok("Password changed successfully");
	}
	
	//checking if email is available
	@GetMapping("/check-mail")
	public ResponseEntity<Boolean> checkMail(@RequestParam String email){
		boolean isAvailable=userService.isEmailAvailable(email);
		
		return ResponseEntity.ok(isAvailable);
	}
	
	//checking if a username is available
	@GetMapping("/check-username")
	public ResponseEntity<Boolean> checkUsername(@RequestParam String username){
		boolean isAvailable=userService.userNameAvailable(username);
		return ResponseEntity.ok(isAvailable);
	}
	
	//update profile image url for user
	@PatchMapping("/{userId}/update-profile-image")
	public ResponseEntity<String> updateUserProfileImage(@PathVariable Long userId, @RequestParam String profileImageUrl){
		userService.updateProfileImage(userId, profileImageUrl);
		return ResponseEntity.ok("Profile Image URL updated for user with ID "+userId);
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
