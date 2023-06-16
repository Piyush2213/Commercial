package com.mycompany.ecommerce.Auth;

import com.mycompany.ecommerce.Repository.CustomerRepository;
import com.mycompany.ecommerce.customer.Customer;
import com.mycompany.ecommerce.customer.CustomerSignUpDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private CustomerRepository customerRepository;

    private String generateToken(Customer customer) {
        // Generate a secure key for HS512 algorithm
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();

        // Use the generated key to sign the token
        return Jwts.builder()
                .setSubject(customer.getEmail())
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail());
        if (customer == null || !customer.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Generate a token for the customer
        String token = generateToken(customer);

        // Return the token in the response
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createNewEmployee(@RequestBody CustomerSignUpDetails customer) {
        String Email = customer.getEmail();
        Customer existing = customerRepository.findByEmail(Email);
        if (existing != null) {
            throw new RuntimeException("Customer with email " + Email + " already exists.");
        }
        Customer savedCustomer = customerRepository.save(new Customer(customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getPassword(), customer.getPhone(), customer.getAddress(), customer.getCity(), customer.getState(), customer.getPostalCode(), customer.getCountry()));
        return ResponseEntity.ok(savedCustomer);
    }


}

