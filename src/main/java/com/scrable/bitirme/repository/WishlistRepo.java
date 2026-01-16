package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Product;
import com.scrable.bitirme.model.User;
import com.scrable.bitirme.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepo extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);

}
