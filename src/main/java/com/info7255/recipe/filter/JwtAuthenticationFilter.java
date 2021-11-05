package com.info7255.recipe.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info7255.recipe.util.JwtUtil;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(ObjectMapper mapper, JwtUtil jwtUtil) {
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return "/token".equals(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");
        System.out.println("filter called");
        if (authorizationHeader == null) {
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", " Token required");

            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(httpServletResponse.getWriter(),errorDetails);
            return;
        }

        boolean isValid;
        try {
            String token = authorizationHeader.substring(7);
            isValid = jwtUtil.validateToken(token);
        } catch (Exception e) {
            System.out.println(e);
            isValid = false;
        }

        if (!isValid) {

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Invalid token");

            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

            mapper.writeValue(httpServletResponse.getWriter(),errorDetails);
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}