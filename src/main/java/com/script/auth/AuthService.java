package com.script.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final Map<String, AuthUser> users = new ConcurrentHashMap<>();
    private final AuthProperties authProperties;
    private final SecretKey secretKey;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public AuthService(AuthProperties authProperties,
                       @Value("${auth.jwt-secret:changeit-changeit-changeit-changeit-changeit}") String jwtSecret) {
        this.authProperties = authProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        logger.info("jwtSecret:" + jwtSecret);
        logger.info("secretKey:" + this.secretKey);
    }

    @PostConstruct
    public void init() {
        for (AuthUser user : authProperties.getUsers()) {
            users.put(user.getUsername(), user);
        }
    }

    public String login(String username, String password) {
        AuthUser user = users.get(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;
        }
        String token = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();

        this.logger.info("user:" + username + " token:" + token);

        return token;
    }

    public String validateToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
