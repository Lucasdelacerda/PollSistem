package com.scrimet.polls.users.controllers;

import com.scrimet.polls.users.dtos.CreateUserDTO;
import com.scrimet.polls.users.dtos.LoginDTO;
import com.scrimet.polls.users.dtos.UserDTO;
import com.scrimet.polls.users.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody CreateUserDTO dto) {
        return userService.createUser(dto);
    }

    @PostMapping("/login")
    public UserDTO login(@Valid @RequestBody LoginDTO dto) {
        return userService.login(dto);
    }
}
