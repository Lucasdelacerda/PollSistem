package com.scrimet.sistemaenquetes.Polls.dtos;

import java.time.LocalDateTime;

public record CreatePollDTO(
String title,
String question,
LocalDateTime finishDate
) {
}