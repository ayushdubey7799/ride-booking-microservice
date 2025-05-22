package com.ridenow.userservice.service.impl;

import com.ridenow.userservice.domain.UserProfileEntity;
import com.ridenow.userservice.dto.UserAuthDto;
import com.ridenow.userservice.repository.UserProfileRepository;
import com.ridenow.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserProfileRepository userProfileRepository;

    @Override
    public List<UserProfileEntity> getAllUsers() {
        List<UserProfileEntity> users =  userProfileRepository.findAll();
        log.info(String.valueOf(users.size()));
        return users;
    }

    @Override
    public void createProfile(UserAuthDto userAuthDto) {
        userProfileRepository.save(UserProfileEntity.newInstance(userAuthDto));
        log.info("Profile created " + userAuthDto.getUsername());
    }
}
