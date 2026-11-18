package com.scrimet.polls.users.dtos;

import com.scrimet.polls.users.infrastructure.models.Users;

public record UserDTO(Long id, String name, String email) {
    public UserDTO(Users user) {
        this(user.getId(), user.getName(), user.getEmail());
    }
}
