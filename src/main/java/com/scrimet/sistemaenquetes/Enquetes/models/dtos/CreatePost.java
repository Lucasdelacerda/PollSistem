package com.scrimet.sistemaenquetes.Enquetes.models.dtos;

import java.time.LocalDateTime;

public record CreatePost(
String titulo,
String pergunta,
LocalDateTime dataEncerramento
) {
}
