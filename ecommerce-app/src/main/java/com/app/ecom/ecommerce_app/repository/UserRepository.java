package com.app.ecom.ecommerce_app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.ecom.ecommerce_app.model.Language;
import com.app.ecom.ecommerce_app.model.MembershipLevel;
import com.app.ecom.ecommerce_app.model.Role;
import com.app.ecom.ecommerce_app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	//find user by username
    Optional<User> findByUsername(String username);
    
    //find user by email
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(@Param("email")String email);
    
    //find user by phone number
    Optional<User> findByPhoneNumber(String phoneNumber);

    //find user by alternate email
    Optional<User> findByAlternateEmail(String alternateEmail);
    
    //find users by membership level
    //needs pagination for large datasets
    List<User> findByMembershipLevel(MembershipLevel membershipLevel);
    
    //list users by Role
    //needs pagination for large datasets
    List<User> findByRole(Role role);
    
    //list users by date of birth
    List<User> findByDateOfBirth(LocalDate dateOfBirth);

    //list users by preferred language
    List<User> findByPreferredLanguage(Language language);
    
    //list users by account status
    List<User> findByAccountStatus(Boolean accountStatus);
    
    //find user by username and email
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.email = :email")
    Optional<User> findByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    //list user by role and membership level
    List<User> findByRoleAndMembershipLevel(Role role, MembershipLevel membershipLevel);

    //find reward points using user Id
    @Query("SELECT u.rewardPoints FROM User u WHERE u.id = :userId")
	Optional<Long> findRewardPointsById(Long userId);
    
    
    
}
