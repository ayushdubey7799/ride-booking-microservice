package com.ridenow.userservice.dto;

import lombok.Data;

@Data
public class UserAuthDto {

    private Long id;

    private String username;

    private String email;

}