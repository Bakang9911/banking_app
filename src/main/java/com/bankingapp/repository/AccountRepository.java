package com.bankingapp.repository;

import com.bankingapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserUsername(String username);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);
}
