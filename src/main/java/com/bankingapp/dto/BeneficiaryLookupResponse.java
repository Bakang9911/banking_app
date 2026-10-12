package com.bankingapp.dto;

public class BeneficiaryLookupResponse {

    private String accountNumber;
    private String name;
    private String type;
    private String currency;

    public BeneficiaryLookupResponse() {
    }

    public BeneficiaryLookupResponse(
            String accountNumber,
            String name,
            String type,
            String currency) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.type = type;
        this.currency = currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCurrency() {
        return currency;
    }
}
