package com.bankingapp.controller;

import com.bankingapp.entity.Account;
import com.bankingapp.service.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(
            @RequestBody Account account,
            Authentication authentication) {

        String username = authentication.getName();

        return accountService.createAccount(
                account,
                username
        );
    }

    @GetMapping
    public List<Account> getAccounts(
            Authentication authentication) {

        String username = authentication.getName();

        return accountService.getAccountsForUser(username);
    }
}