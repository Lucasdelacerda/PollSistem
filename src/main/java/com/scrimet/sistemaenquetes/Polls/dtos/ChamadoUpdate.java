package com.scrimet.sistemaenquetes.Polls.dtos;

import com.scrimet.sistemaenquetes.Polls.enums.Status;

public record ChamadoUpdate(
        Long usuarioId,
        Status status
) {

}
