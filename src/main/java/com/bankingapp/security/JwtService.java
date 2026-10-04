package com.bankingapp.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private static final long EXPIRATION_SECONDS = 3600;

    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(
            String username,
            String role) {

        Instant now = Instant.now();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .claim("role", role)
                        .issuedAt(now)
                        .expiresAt(
                                now.plusSeconds(
                                        EXPIRATION_SECONDS
                                )
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters
                                .from(
                                        claims
                                )
                )
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return EXPIRATION_SECONDS;
    }
}