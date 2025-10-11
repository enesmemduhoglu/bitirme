package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
