package com.ridenow.authservice.service;

import com.ridenow.authservice.domain.UserAuthEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthService extends UserDetailsService {
    void save(UserAuthEntity userAuthEntity);
}
