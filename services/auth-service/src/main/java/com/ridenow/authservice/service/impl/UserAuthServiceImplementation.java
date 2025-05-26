package com.ridenow.authservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ridenow.authservice.domain.Role;
import com.ridenow.authservice.domain.UserAuthEntity;
import com.ridenow.authservice.dto.UserAuthDto;
import com.ridenow.authservice.messaging.kafka.KafkaProducerService;
import com.ridenow.authservice.repository.UserAuthEntityRepository;
import com.ridenow.authservice.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAuthServiceImplementation implements UserAuthService {

    @Autowired
    UserAuthEntityRepository userAuthEntityRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAuthEntityRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    @Override
    public void save(UserAuthEntity userAuthEntity) {
        userAuthEntity.setPassword(encoder.encode(userAuthEntity.getPassword()));
        userAuthEntity = userAuthEntityRepository.save(userAuthEntity);
        try{
            kafkaProducerService.sendMessage("create-profile-topic", UserAuthDto.newInstance(userAuthEntity));
        }
        catch (JsonProcessingException e){
            log.error("Exception occured " + e);
        }
    }

    @Override
    public void updateStatusToDriver(long l) {
        UserAuthEntity userAuthEntity = userAuthEntityRepository.findById(l).orElse(null);
        if(userAuthEntity!=null){
            userAuthEntity.setRole(Role.DRIVER);
            userAuthEntityRepository.save(userAuthEntity);
        }
    }
}
