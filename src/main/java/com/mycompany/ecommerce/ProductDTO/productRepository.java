package com.mycompany.ecommerce.ProductDTO;

import org.springframework.data.jpa.repository.JpaRepository;

public interface productRepository extends JpaRepository<Product, Integer> {
    Product findProductById(Integer id);
}
