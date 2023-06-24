package com.mycompany.ecommerce.Orders;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table (name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private int orderid;

    @Column(name = "orderdate")
    private Date orderdate;

    @Column(name = "totalamount")
    private BigDecimal totalamount;

    @Column(name = "customerid")
    private int customerid;
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public int getOrderId() {
        return orderid;
    }

    public void setOrderId(int orderid) {
        this.orderid = orderid;
    }

    public Date getOrderdate() {
        return orderdate;
    }

    public void setOrderDate(Date orderdate) {
        this.orderdate = orderdate;
    }

    public BigDecimal getTotalAmount() {
        return totalamount;
    }

    public void setTotalAmount(BigDecimal totalamount) {
        this.totalamount = totalamount;
    }

    public int getCustomerId() {
        return customerid;
    }

    public void setCustomerId(int customerid) {
        this.customerid = customerid;
    }



    public Orders() {
    }

    public Orders(Date orderdate, BigDecimal totalamount, int customerid, List<OrderItem> orderItems) {
        this.orderdate = orderdate;
        this.totalamount = totalamount;
        this.customerid = customerid;
        this.orderItems = orderItems;
    }

}
