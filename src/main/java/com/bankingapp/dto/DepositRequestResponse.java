package com.bankingapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositRequestResponse {

    private Long id;
    private Long accountId;
    private String accountNumber;
    private String accountName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    public DepositRequestResponse() {
    }

    public DepositRequestResponse(
            Long id,
            Long accountId,
            String accountNumber,
            String accountName,
            BigDecimal amount,
            String status,
            LocalDateTime requestedAt,
            LocalDateTime processedAt) {

        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.amount = amount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}