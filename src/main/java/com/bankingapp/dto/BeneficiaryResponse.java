package com.bankingapp.dto;

import java.time.LocalDateTime;

public class BeneficiaryResponse {

    private Long id;
    private String nickname;
    private String accountNumber;
    private String name;
    private String type;
    private String currency;
    private LocalDateTime createdAt;

    public BeneficiaryResponse(
            Long id,
            String nickname,
            String accountNumber,
            String name,
            String type,
            String currency,
            LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.accountNumber = accountNumber;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
