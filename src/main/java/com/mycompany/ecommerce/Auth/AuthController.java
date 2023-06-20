package com.mycompany.ecommerce.Auth;

import com.mycompany.ecommerce.Admin.Admin;
import com.mycompany.ecommerce.Admin.AdminSignUpDetails;
import com.mycompany.ecommerce.Repository.CustomerRepository;
import com.mycompany.ecommerce.Repository.adminRepository;
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
    @Autowired
    private adminRepository adminRepository;


    @PostMapping("/customer/login")
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
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody AuthRequest request) {
        // Check if the request contains a token
        if (request.getToken() != null) {
            Admin admin = adminRepository.findByToken(request.getToken());
            if (admin != null) {
                return ResponseEntity.ok(admin);
            }
        }

        Admin admin = adminRepository.findByAdminEmail(request.getEmail());
        if (admin == null || !admin.getAdminPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String token = generateToken(admin);

        return ResponseEntity.ok(new AuthResponse(token, null));
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
    private String generateToken(Admin admin) {
        byte[] keyBytes = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();

        String token = Jwts.builder()
                .setSubject(admin.getAdminEmail())
                .signWith(SignatureAlgorithm.HS512, keyBytes)
                .compact();

        admin.setToken(token);
        adminRepository.updateToken(admin.getId(), token);

        return token;
    }




    @PostMapping("/customer/signup")
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

    @PostMapping("/admin/signup")
    public ResponseEntity<?> createNewAdmin(@RequestBody AdminSignUpDetails admin) {
        String email = admin.getEmail();
        Admin existingAdmin = adminRepository.findByAdminEmail(email);

        if (existingAdmin != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Admin with email " + email + " already exists.");
        }

        Admin newAdmin = new Admin();
        newAdmin.setName(admin.getName());
        newAdmin.setAdminEmail(admin.getEmail());
        newAdmin.setAdminPassword(admin.getPassword());

        Admin savedAdmin = adminRepository.save(newAdmin);

        return ResponseEntity.ok(savedAdmin);
    }

}

