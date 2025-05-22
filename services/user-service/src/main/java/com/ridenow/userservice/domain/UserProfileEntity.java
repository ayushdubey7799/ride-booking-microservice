package com.ridenow.userservice.domain;

import com.ridenow.userservice.dto.UserAuthDto;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Entity
@Data
@Table(name = "user_profile")
public class UserProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    private Long userAuthId;

    public static UserProfileEntity newInstance(UserAuthDto userAuthDto) {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        BeanUtils.copyProperties(userAuthDto,userProfileEntity,"id");
        userProfileEntity.setUserAuthId(userAuthDto.getId());
        return userProfileEntity;
    }
}