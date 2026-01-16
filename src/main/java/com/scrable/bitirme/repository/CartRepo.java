package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Cart;
import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart,Long> {
    List<Cart> findByUser(User user);

    List<Cart> findAll();

    Optional<Cart> findByUserAndProduct(User user, Product product);
}
