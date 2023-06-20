package com.mycompany.ecommerce.Repository;

import com.mycompany.ecommerce.Admin.Admin;
import com.mycompany.ecommerce.customer.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface adminRepository extends JpaRepository<Admin, Integer> {


    Admin findByAdminEmail(String adminEmail);

    @Transactional
    @Modifying
    @Query("UPDATE Admin a SET a.token = ?2 WHERE a.id = ?1")
    int updateToken(int adminId, String token);

    @Query("SELECT a FROM Admin a WHERE a.token = ?1")
    Admin findByToken(String token);



}
