package com.info7255.recipe.service;

import com.info7255.recipe.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthService {

    @Autowired
    private JwtUtil jwtUtil;

    public String generateToken() {
        // Generating Token

        return jwtUtil.generateToken();
    }
}
