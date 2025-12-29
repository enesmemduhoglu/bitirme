package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}
