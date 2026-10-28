package com.scrimet.sistemaenquetes.Users.dtos;


import jakarta.validation.constraints.Pattern;

public record CreateUserDTO(
        String name,
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com(\\.br)?$", message = "E-mail inválido. Deve conter @ e terminar com .com")
        String email
) {
}
