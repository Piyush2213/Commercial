package com.mycompany.ecommerce.Auth;
import java.math.BigDecimal;

import com.mycompany.ecommerce.Admin.Admin;
import com.mycompany.ecommerce.Admin.AdminSignUpDetails;
import com.mycompany.ecommerce.Orders.OrderItem;
import com.mycompany.ecommerce.Orders.OrderRepository;
import com.mycompany.ecommerce.Orders.Orders;
import com.mycompany.ecommerce.ProductDTO.Product;
import com.mycompany.ecommerce.ProductDTO.productRepository;
import com.mycompany.ecommerce.Repository.CustomerRepository;
import com.mycompany.ecommerce.Repository.adminRepository;
import com.mycompany.ecommerce.customer.Customer;
import com.mycompany.ecommerce.customer.CustomerSignUpDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
    @Autowired
    private productRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/customer/orders")
    public ResponseEntity<?> placeOrder(@RequestBody Orders orders, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Customer customer = customerRepository.findByToken(token);
        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid customer token");
        }

        List<OrderItem> orderItems = orders.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order items are missing");
        }

        Orders newOrder = new Orders();
        newOrder.setCustomerId(customer.getId());
        newOrder.setOrderItems(orderItems);

        newOrder.setOrderDate(new Date());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProduct().getId();
            if (productId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product ID is missing in order item");
            }

            Product fetchedProduct = productRepository.findProductById(productId);
            if (fetchedProduct == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found for order item");
            }

            BigDecimal itemPrice = BigDecimal.valueOf((double) fetchedProduct.getPrice()).multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount = totalAmount.add(itemPrice);
        }

        newOrder.setTotalAmount(totalAmount);

        Orders savedOrder = orderRepository.save(newOrder);


        return ResponseEntity.ok(savedOrder);
    }





    @PostMapping("/admin/products/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        Admin admin = adminRepository.findByToken(token);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin token");
        }
        Product new_product = new Product();
        new_product.setName(product.getName());
        new_product.setDescription(product.getDescription());
        new_product.setPrice(product.getPrice());
        new_product.setQuantity(product.getQuantity());

        Product savedProduct = productRepository.save(product);

        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("name", product.getName());
            productInfo.put("price", product.getPrice());
            response.add(productInfo);
        }

        return ResponseEntity.ok(response);
    }



    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Integer productId){
        Product existingProduct = productRepository.findProductById(productId);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        return ResponseEntity.ok(existingProduct);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer productId, @RequestBody Product product, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Admin admin = adminRepository.findByToken(token);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin token");
        }

        Product existingProduct = productRepository.findProductById(productId);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        Product updatedProduct = productRepository.save(existingProduct);

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId, @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        Admin admin = adminRepository.findByToken(token);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid admin token");
        }
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
        productRepository.deleteById(productId);

        return ResponseEntity.ok("Product deleted successfully");
    }




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

