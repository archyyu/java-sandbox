package com.script.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthFilter implements Filter {

    static final String USER_ATTRIBUTE = "authUser";

    private final AuthService authService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

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
            String username = authService.validateToken(token);
            logger.info("token:" + token + " username:" + username);
            if (username == null) {
                res.setStatus(401);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"invalid token\"}");
                return;
            }
            req.setAttribute(USER_ATTRIBUTE, username);
        }

        chain.doFilter(request, response);
    }
}
