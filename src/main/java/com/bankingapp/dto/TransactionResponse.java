package com.bankingapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private String type;
    private String direction;
    private String description;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String relatedAccountNumber;
    private String relatedAccountName;
    private String reference;

    public TransactionResponse() {
    }

    public TransactionResponse(
            Long id,
            String type,
            String direction,
            String description,
            BigDecimal amount,
            LocalDateTime timestamp,
            String relatedAccountNumber,
            String relatedAccountName) {

        this(
                id, type, direction, description, amount, timestamp,
                relatedAccountNumber, relatedAccountName, null
        );
    }

    public TransactionResponse(
            Long id,
            String type,
            String direction,
            String description,
            BigDecimal amount,
            LocalDateTime timestamp,
            String relatedAccountNumber,
            String relatedAccountName,
            String reference) {

        this.id = id;
        this.type = type;
        this.direction = direction;
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
        this.relatedAccountNumber = relatedAccountNumber;
        this.relatedAccountName = relatedAccountName;
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getRelatedAccountNumber() {
        return relatedAccountNumber;
    }

    public String getRelatedAccountName() {
        return relatedAccountName;
    }

    public String getReference() {
        return reference;
    }
}