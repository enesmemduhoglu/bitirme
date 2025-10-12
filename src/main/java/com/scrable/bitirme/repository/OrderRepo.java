package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Order;
import com.scrable.bitirme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
