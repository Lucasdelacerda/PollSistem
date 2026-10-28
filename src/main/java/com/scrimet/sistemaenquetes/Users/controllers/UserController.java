package com.scrimet.sistemaenquetes.Users.controllers;

import com.scrimet.sistemaenquetes.Users.dtos.CreateUserDTO;
import com.scrimet.sistemaenquetes.Users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService  userService;

    @PostMapping(value = "/register")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDTO dto){
        try{
            userService.createUser(dto);
            return ResponseEntity.ok("Cliente cadastrado");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
