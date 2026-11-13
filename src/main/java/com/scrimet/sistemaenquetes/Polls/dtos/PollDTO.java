package com.scrimet.sistemaenquetes.Polls.dtos;

import com.scrimet.sistemaenquetes.Polls.infrainstructure.models.Poll;
import com.scrimet.sistemaenquetes.Polls.enums.Status;

import java.time.LocalDateTime;

public record PollDTO(
        Long id,
        String title,
        String question,
        Status status,
        Long userId,
        LocalDateTime finishDate,
        LocalDateTime creationDate
) {
    public PollDTO(Poll poll) {
        this(
                poll.getId(),
                poll.getTitle(),
                poll.getQuestion(),
                poll.getStatus(),
                poll.getUsers().getId(),
                poll.getFinishDate(),
                poll.getCreationDate()
        );
    }


}
