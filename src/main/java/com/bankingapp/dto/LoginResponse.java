package com.bankingapp.dto;

public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresIn;
    private String username;
    private String role;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String tokenType,
            long expiresIn,
            String username,
            String role) {

        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}