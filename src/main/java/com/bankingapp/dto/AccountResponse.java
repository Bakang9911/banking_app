package com.bankingapp.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private String name;
    private String type;

    public AccountResponse() {
    }

    public AccountResponse(
            Long id,
            String accountNumber,
            BigDecimal balance,
            String currency,
            String name,
            String type) {

        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
