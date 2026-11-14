package com.scrimet.sistemaenquetes.Polls.dtos;

import com.scrimet.sistemaenquetes.Polls.infrainstructure.models.Poll;

import java.time.LocalDateTime;

public record ListAllPollsDTO(
        Long id,
        String title,
        LocalDateTime finishDate
) {
    public ListAllPollsDTO(Poll polls){
        this(
                polls.getId(),
                polls.getTitle(),
                polls.getFinishDate()

        );

    }
}
