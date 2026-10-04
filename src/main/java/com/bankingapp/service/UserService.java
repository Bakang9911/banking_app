package com.bankingapp.service;

import com.bankingapp.entity.Account;
import com.bankingapp.entity.User;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(
            String name,
            String surname,
            String nationalId,
            String cellNumber,
            String username,
            String password) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        if (userRepository.existsByNationalId(nationalId)) {
            throw new IllegalArgumentException(
                    "National ID already exists"
            );
        }

        if (userRepository.existsByCellNumber(cellNumber)) {
            throw new IllegalArgumentException(
                    "Cell number already exists"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(password);

        User user = new User(
                username,
                encodedPassword,
                "CUSTOMER",
                name,
                surname,
                nationalId,
                cellNumber
        );

        User savedUser = userRepository.save(user);

        Account account = new Account();

        account.setName(name + " " + surname);
        account.setAccountNumber(generateAccountNumber());
        account.setType("SAVINGS");
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("ZAR");
        account.setUser(savedUser);

        accountRepository.save(account);

        return savedUser;
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = String.valueOf(
                    10000000L +
                            (long) (Math.random() * 90000000L)
            );

        } while (
                accountRepository.existsByAccountNumber(accountNumber)
        );

        return accountNumber;
    }
}