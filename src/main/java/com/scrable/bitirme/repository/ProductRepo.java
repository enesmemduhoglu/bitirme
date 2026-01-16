package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findByProductName(String name);

    Optional<Product> findById(Long id);

    List<Product> findAll();
}
