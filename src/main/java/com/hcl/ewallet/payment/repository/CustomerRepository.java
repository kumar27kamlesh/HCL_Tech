package com.hcl.ewallet.payment.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hcl.ewallet.payment.entity.Customer;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by business customerId
    Optional<Customer> findByCustomerId(String customerId);

    // Find customers by status
    List<Customer> findByStatus(String status);

    // Check if customer exists
    boolean existsByCustomerId(String customerId);
}
