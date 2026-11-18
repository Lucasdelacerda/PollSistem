package com.scrimet.polls.polls.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record CreatePollDTO(
        @NotBlank String title,
        @NotBlank String question,
        @NotNull @Future LocalDateTime finishDate,
        @NotEmpty @Size(min = 2) List<@NotBlank String> options
) {
}