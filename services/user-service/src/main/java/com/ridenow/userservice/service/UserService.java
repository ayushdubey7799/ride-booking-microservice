package com.ridenow.userservice.service;

import com.ridenow.userservice.domain.UserProfileEntity;
import com.ridenow.userservice.dto.UserAuthDto;

import java.util.List;

public interface UserService {

    List<UserProfileEntity> getAllUsers();

    void createProfile(UserAuthDto userAuthDto);

}
