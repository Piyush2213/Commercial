package com.mycompany.ecommerce.Auth;

import com.mycompany.ecommerce.customer.Customer;

public class AuthResponse {
    private String token;
    private Customer customer;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public AuthResponse(String token, Customer customer) {
        this.token = token;
        this.customer = customer;
    }
}
