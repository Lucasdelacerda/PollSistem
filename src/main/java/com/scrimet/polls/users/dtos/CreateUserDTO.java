package com.scrimet.polls.users.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank String name,
        @NotBlank @Email
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.com$", flags = Pattern.Flag.CASE_INSENSITIVE)
        String email,
        @NotBlank @Size(min = 6) String password
) {
}
