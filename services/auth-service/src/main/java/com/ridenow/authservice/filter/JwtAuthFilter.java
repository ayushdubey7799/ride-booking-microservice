package com.ridenow.authservice.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridenow.authservice.dto.LoginRequest;
import com.ridenow.authservice.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public JwtAuthFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
        this.authenticationManager =authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getServletPath().equals("/api/auth/login")){
            filterChain.doFilter(request,response);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(),LoginRequest.class);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword());
        Authentication authResult = authenticationManager.authenticate(authenticationToken);
        if(authResult.isAuthenticated()){
            String token = jwtUtil.generateToken(authResult.getName(),15);
            response.setHeader("Authorization", "Bearer " + token);
        }
    }
}
