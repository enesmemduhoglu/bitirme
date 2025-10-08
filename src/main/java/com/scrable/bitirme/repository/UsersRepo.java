package com.scrable.bitirme.repository;

import com.scrable.bitirme.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findById(Long id);

    List<Users> findAll();

    Optional<Users> findByVerificationCode(String verificationCode);

    Optional<Users> findByEmail(String email);
}
