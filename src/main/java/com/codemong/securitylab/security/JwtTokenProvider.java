package com.codemong.securitylab.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity-ms}") long accessTokenValidityMs,
            @Value("${jwt.refresh-token-validity-ms}") long refreshTokenValidityMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;
    }

    public String createAccessToken(String subject) {
        return createToken(subject, "ACCESS", accessTokenValidityMs);
    }

    public String createRefreshToken(String subject) {
        return createToken(subject, "REFRESH", refreshTokenValidityMs);
    }

    public Optional<String> getSubjectIfValidAccessToken(String token) {
        return getSubjectIfValidType(token, "ACCESS");
    }

    public Optional<String> getSubjectIfValidRefreshToken(String token) {
        return getSubjectIfValidType(token, "REFRESH");
    }

    private String createToken(String subject, String type, long validityMs) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityMs);
        return Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    private Optional<String> getSubjectIfValidType(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (!expectedType.equals(claims.get("type", String.class))) {
                return Optional.empty();
            }
            return Optional.ofNullable(claims.getSubject());
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
