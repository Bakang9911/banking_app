package com.bankingapp.dto;

public class SaveBeneficiaryRequest {

    private String accountNumber;
    private String nickname;

    public SaveBeneficiaryRequest() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
