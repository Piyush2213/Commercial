package com.mycompany.ecommerce.Orders;

import com.mycompany.ecommerce.ProductDTO.Product;
import jakarta.persistence.*;

    @Entity
    @Table(name = "orderitem")
    public class OrderItem {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "orderitemid")
        private Integer orderitemid;



        @ManyToOne
        @MapsId("orderid")
        @JoinColumn(name = "orderid")
        private Orders orders;


        @ManyToOne
        @JoinColumn(name = "productid")
        private Product product;

        @Column(name = "Quantity")
        private Integer quantity;

        public Integer getOrderitemid() {
            return orderitemid;
        }

        public void setOrderitemid(Integer orderitemid) {
            this.orderitemid = orderitemid;
        }

        public void setOrder(Orders orders) {
            this.orders = orders;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public OrderItem(Orders orders, Product product, Integer quantity, Integer orderitemid) {
            this.orders = orders;
            this.product = product;
            this.quantity = quantity;
            this.orderitemid = orderitemid;
        }
        public OrderItem(){};

    }
