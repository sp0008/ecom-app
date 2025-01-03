package com.app.ecom.ecommerce_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.ecom.ecommerce_app.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
