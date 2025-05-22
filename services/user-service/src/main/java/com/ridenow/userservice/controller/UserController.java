package com.ridenow.userservice.controller;

import com.ridenow.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

   @GetMapping("/all")
   public ResponseEntity<?> getAll(){
       log.info("API CALLED............");
       return ResponseEntity.ok(userService.getAllUsers());
   }
}
