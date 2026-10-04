package com.bankingapp.controller;

import com.bankingapp.dto.AccountResponse;
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

    @GetMapping
    public List<AccountResponse> getAccounts(
            Authentication authentication) {

        String username = authentication.getName();

        return accountService.getAccountsForUser(username)
                .stream()
                .map(AccountController::toResponse)
                .toList();
    }

    private static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency(),
                account.getName(),
                account.getType()
        );
    }
}