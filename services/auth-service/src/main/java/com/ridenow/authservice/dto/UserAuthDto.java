package com.ridenow.authservice.dto;

import com.ridenow.authservice.domain.UserAuthEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserAuthDto {

    private Long id;

    private String username;

    private String email;

    public static UserAuthDto newInstance(UserAuthEntity userAuthEntity) {
        UserAuthDto dto = new UserAuthDto();
        BeanUtils.copyProperties(userAuthEntity,dto);
        return dto;
    }
}