package com.script.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final Map<String, Boolean> tokens = new ConcurrentHashMap<>();

    private final String password;

    public AuthService(@Value("${auth.password:secret}") String password) {
        this.password = password;
    }

    public String login(String password) {
        if (!this.password.equals(password)) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        tokens.put(token, true);
        return token;
    }

    public boolean validateToken(String token) {
        return token != null && tokens.containsKey(token);
    }

    public void logout(String token) {
        tokens.remove(token);
    }
}
