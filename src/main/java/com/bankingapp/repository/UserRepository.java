package com.bankingapp.repository;

import com.bankingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByNationalId(String nationalId);

    boolean existsByCellNumber(String cellNumber);
}