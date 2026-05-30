package com.script.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthFilter implements Filter {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (path.startsWith("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        if (path.startsWith("/api/")) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"missing or invalid token\"}");
                return;
            }
            String token = authHeader.substring(7);
            if (!authService.validateToken(token)) {
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"invalid token\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
