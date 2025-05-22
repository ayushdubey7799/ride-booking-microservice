package com.ridenow.authservice.security.config;

import com.ridenow.authservice.filter.JwtAuthFilter;
import com.ridenow.authservice.filter.JwtValidationFilter;
import com.ridenow.authservice.security.authenticationProviders.JwtAuthenticationProvider;
import com.ridenow.authservice.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private JWTUtil jwtUtil;
    private UserDetailsService userDetailsService;
    private PasswordEncoder encoder;

    @Autowired
    public SecurityConfig(JWTUtil jwtUtil,UserDetailsService userDetailsService, PasswordEncoder encoder){
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(){
        return new JwtAuthenticationProvider(jwtUtil,userDetailsService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,JWTUtil jwtUtil) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(authenticationManager,jwtUtil);
        JwtValidationFilter jwtValidationFilter = new JwtValidationFilter(authenticationManager);
        http
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/*").permitAll()
                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtValidationFilter, JwtAuthFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(Arrays.asList(jwtAuthenticationProvider(),daoAuthenticationProvider()));
    }
}