package com.ridenow.authservice.service.impl;

import com.ridenow.authservice.domain.UserAuthEntity;
import com.ridenow.authservice.repository.UserAuthEntityRepository;
import com.ridenow.authservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserAuthEntityRepository userAuthEntityRepository;

    @Override
    public List<UserAuthEntity> getAllUsers() {
        List<UserAuthEntity> users =  userAuthEntityRepository.findAll();
        log.info(String.valueOf(users.size()));
        return users;
    }
}
