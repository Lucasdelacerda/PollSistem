package com.scrimet.polls.users.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDTO(
        @NotBlank String name,
        @NotBlank @Email
        String email
) {
}
