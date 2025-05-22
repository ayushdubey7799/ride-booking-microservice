package com.ridenow.authservice.service;

import com.ridenow.authservice.domain.UserAuthEntity;

import java.util.List;

public interface UserService {

    List<UserAuthEntity> getAllUsers();
}
