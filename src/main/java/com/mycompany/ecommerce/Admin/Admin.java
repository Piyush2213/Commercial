package com.mycompany.ecommerce.Admin;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminid")
    private int id;

    @Column(name = "adminname")
    private String name;

    @Column(name = "adminemail", unique = true)
    private String adminEmail;

    @Column(name = "adminpassword")
    private String adminPassword;

    @Column(name = "token")
    private String token;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Admin(int id, String name, String adminEmail, String adminPassword) {
        this.id = id;
        this.name = name;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    public Admin() {}
}