package com.mycompany.ecommerce.Auth;

import com.mycompany.ecommerce.Repository.CustomerRepository;
import com.mycompany.ecommerce.customer.Customer;
import com.mycompany.ecommerce.customer.CustomerSignUpDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
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


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        if (request.getToken() != null) {
            Customer customer = customerRepository.findByToken(request.getToken());
            if (customer != null) {
                return ResponseEntity.ok(customer);
            }
        }

        Customer customer = customerRepository.findByEmail(request.getEmail());
        if (customer == null || !customer.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }


        String token = generateToken(customer);

        return ResponseEntity.ok(new AuthResponse(token,null));
    }

    private String generateToken(Customer customer) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();

        String token = Jwts.builder()
                .setSubject(customer.getEmail())
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();

        customer.setToken(token);
        customerRepository.updateToken(customer.getId(), token);

        return token;
    }




    @PostMapping("/signup")
    public ResponseEntity<?> createNewEmployee(@RequestBody CustomerSignUpDetails customer) {
        String email = customer.getEmail();
        Customer existing = customerRepository.findByEmail(email);

        if (existing != null) {
            throw new RuntimeException("Customer with email " + email + " already exists.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(customer.getFirstName());
        newCustomer.setLastName(customer.getLastName());
        newCustomer.setEmail(email);
        newCustomer.setPassword(customer.getPassword());
        newCustomer.setPhone(customer.getPhone());
        newCustomer.setAddress(customer.getAddress());


        Customer savedCustomer = customerRepository.save(newCustomer);

        return ResponseEntity.ok(savedCustomer);
    }



}

