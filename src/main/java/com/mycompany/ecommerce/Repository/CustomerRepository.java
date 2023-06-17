package com.mycompany.ecommerce.Repository;

import com.mycompany.ecommerce.customer.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Customer c SET c.token = ?2 WHERE c.id = ?1")
    int updateToken(int customerId, String token);

    @Query("SELECT c FROM Customer c WHERE c.token = ?1")
    Customer findByToken(String token);


}

