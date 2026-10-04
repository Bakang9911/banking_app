package com.bankingapp.service;

import com.bankingapp.entity.Account;
import com.bankingapp.entity.User;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(
            Account account,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found"
                        ));

        // One account per customer
        if (accountRepository.existsByUserId(user.getId())) {
            throw new IllegalArgumentException(
                    "Customer already has an account"
            );
        }

        account.setUser(user);

        return accountRepository.save(account);
    }

    public List<Account> getAccountsForUser(String username) {
        return accountRepository.findByUserUsername(username);
    }
}