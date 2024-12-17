package com.app.ecom.ecommerce_app.service;

import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

@Service
public class UserService {

	
	private final UserRepository userRepo;
	private final PasswordEncoder pwdEncoder; //to encode the password
	private final AddressRepository addressRepo;
	private final ProductRepository productRepo;
	
	public UserService(UserRepository userRepo, PasswordEncoder pwdEncoder, AddressRepository addressRepo, ProductRepository productRepo) {
		this.userRepo=userRepo;
		this.pwdEncoder=pwdEncoder;
		this.addressRepo=addressRepo;
		this.productRepo=productRepo;
	}
	
	
	//register new user
	public User registerUser(User user) {
		user.setPassword(pwdEncoder.encode(user.getPassword()));
		user.setAccountStatus(true);
		user.setRegistrationDate(LocalDate.now());
		return userRepo.save(user);
	}

	// find user using user name
	public Optional<User> findByUsername(String username){
		return userRepo.findByUsername(username);
	}
	
	//update user details for a specific user
	public User updateUserDetails(Long userId, User updatedUserDetails) {
		
		User existingUser=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User not found with ID: "+userId));
		 
		
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
		        // Validate currency if necessary
		        existingUser.setCurrency(updatedUserDetails.getCurrency());
		    }

		    if (updatedUserDetails.getMembershipLevel() != null) {
		        // Allow membership changes only if rules are met
		        if (isMembershipChangeAllowed(existingUser, updatedUserDetails.getMembershipLevel())) {
		            existingUser.setMembershipLevel(updatedUserDetails.getMembershipLevel());
		        } else {
		            throw new IllegalArgumentException("Membership level change not allowed.");
		        }
		    }

		return userRepo.save(existingUser);
	}
	
	//method to check if membership change is allowed
	private boolean isMembershipChangeAllowed(User existingUser, MembershipLevel newMembershipLevel) {
	    MembershipLevel currentMembershipLevel = existingUser.getMembershipLevel();

	    // Allow downgrade from "Premium" to any lower level
	    if (currentMembershipLevel == MembershipLevel.PREMIUM && 
	        (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.FREE)) {
	        return true; // Downgrade to "Basic" or "Free"
	    }

	    // Allow downgrade from "Pro" to any lower level
	    if (currentMembershipLevel == MembershipLevel.PRO && 
	        (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.FREE)) {
	        return true; // Downgrade to "Basic" or "Free"
	    }

	    // Allow downgrade from "Basic" to "Free"
	    if (currentMembershipLevel == MembershipLevel.BASIC && newMembershipLevel == MembershipLevel.FREE) {
	        return true; // Downgrade to "Free"
	    }

	    // Allow upgrade to "Pro" or "Premium" from "Basic"
	    if (currentMembershipLevel == MembershipLevel.BASIC && 
	        (newMembershipLevel == MembershipLevel.PRO || newMembershipLevel == MembershipLevel.PREMIUM)) {
	        return true; // Upgrade to "Pro" or "Premium"
	    }

	    // Allow upgrade to "Premium" from "Pro"
	    if (currentMembershipLevel == MembershipLevel.PRO && newMembershipLevel == MembershipLevel.PREMIUM) {
	        return true; // Upgrade to "Premium"
	    }

	    // Allow upgrade to "Pro" from "Free"
	    if (currentMembershipLevel == MembershipLevel.FREE && newMembershipLevel == MembershipLevel.PRO) {
	        return true; // Upgrade to "Pro"
	    }

	    // Allow upgrade to "Basic" or "Pro" from "Free"
	    if (currentMembershipLevel == MembershipLevel.FREE && 
	        (newMembershipLevel == MembershipLevel.BASIC || newMembershipLevel == MembershipLevel.PRO)) {
	        return true; // Upgrade to "Basic" or "Pro"
	    }

	    // Allow upgrade to "Premium" from "Basic" or "Pro"
	    if ((currentMembershipLevel == MembershipLevel.BASIC || currentMembershipLevel == MembershipLevel.PRO) && 
	        newMembershipLevel == MembershipLevel.PREMIUM) {
	        return true; // Upgrade to "Premium"
	    }

	    // All other cases: if no restriction is found, return false
	    return false;
	}


	//delete user by using user Id
	public void deleteUser(Long userId) {
		if(!userRepo.existsById(userId)) {
			throw new RuntimeException("user not found with ID "+userId);
		}
		
		userRepo.deleteById(userId);
	}
	
	
	//find user using user email ID
	public Optional<User> findByEmail(String email){
		return userRepo.findByEmail(email);
	}

	//change password of the user
	public void changePwd(Long userId, String oldPwd, String newPwd) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("user not found with ID "+userId));
		
		if(!pwdEncoder.matches(oldPwd, user.getPassword())) {
			throw new IllegalArgumentException("Old password is not correct");
		}
		
		user.setPassword(pwdEncoder.encode(newPwd));
		
		userRepo.save(user);
	}
	
	//add address for a user
	public void addAddress(Long userId, Address address) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID"+ userId));
		address.setUser(user);
		addressRepo.save(address);
	}
	
	//add product to user wishlist
	public void addToWishlist(Long userId, Long productId) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		Product product=productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product not found with ID "+productId));
		
		user.getWishList().add(product);
		userRepo.save(user);
	}

	//add product to user cart
	public void addToCart(Long userId, Long productId) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		Product product=productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product not found with ID "+productId));
		
		user.getCart().add(product);
		userRepo.save(user);
	}
	
	//add product to order history
	public void addToOrderHistory(Long userId, Long productId) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		Product product=productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product not found with ID "+productId));
		
		user.getOrderHistory().add(product);
		userRepo.save(user);
	}
	
	//get user wishlist
	public List<Product> getUserWishlist(Long userId){
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		return user.getWishList();
	}
	
	
	//get user cart
	public List<Product> getUserCart(Long userId){
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		return user.getCart();
	}
	
     //get user order history
	public List<Product> getOrderHistory(Long userId){
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		return user.getOrderHistory();
	}
	
	//deactivate user account
	public void deactivateUserAccount(Long userId) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		user.setAccountStatus(false);
		userRepo.save(user);
	}
	
	
	//assign new role to user
	public User assignUserRole(Long userId, Role newRole) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("user not found with ID "+userId));
		
		user.setRole(newRole);
		return userRepo.save(user);
	}
	
	//list all users
	public List<User> findAllUsers(){
		return userRepo.findAll();
	}
	
	//check if username is present or not
	public boolean userNameAvailable(String username) {
		return userRepo.findByUsername(username).isPresent();
	}
	
	
	//check if email is available
	public boolean isEmailAvailable(String email) {
		return userRepo.findByEmail(email).isPresent();
	}
	
	//find user By ID
	public User findUserById(Long userId) {
		return userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
	}
	
	//allow user to reset password using email
	public void resetPwd(String email, String pwd) {
		User user=userRepo.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found with mail "+ email));
		
		user.setPassword(pwdEncoder.encode(pwd));
		
	   userRepo.save(user);
	}
	
	
	//check user credential
	public boolean checkUserCred(String username, String pwd)
	{
		User user=userRepo.findByUsername(username).orElseThrow(()-> new RuntimeException("User name is invalid"));

		return pwdEncoder.matches(pwd, user.getPassword());
	}
	
	
	//get users by role
	public List<User> getUsersByRole(Role role){
		return userRepo.findAll().stream().filter(user-> user.getRole().equals(role)).collect(Collectors.toList());
	}
	
	
	//find list of users using user name or mail
	public List<User> findUsers(String keyword){
		return userRepo.findAll().stream().filter(user-> user.getUsername().contains(keyword) || 
				user.getEmail().contains(keyword)).collect(Collectors.toList());
	}
	
	//update profileImageUrl for user
	public void updateProfileImage(Long userId, String profileImageUrl) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		
	    user.setProfileImageUrl(profileImageUrl);
	    userRepo.save(user);
	}
	
	//fetch rewards of user
	public Optional<Long> getUserReward(Long userId) {
		User user=userRepo.findById(userId).orElseThrow(()-> new RuntimeException("User not found with ID "+userId));
		
		Optional<Long> rewards=userRepo.findRewardPointsById(userId);
		if(rewards==null) {
			throw new RuntimeException("Rewards not found for user");
		}
		
		return rewards;
	}
	
	
	
}


