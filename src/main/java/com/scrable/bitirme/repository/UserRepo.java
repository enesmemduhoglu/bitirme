package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findAll();

    Optional<User> findByVerificationCode(String verificationCode);

    Optional<User> findByEmail(String email);
}
