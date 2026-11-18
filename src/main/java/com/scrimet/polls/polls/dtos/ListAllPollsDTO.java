package com.scrimet.polls.polls.dtos;

import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.enums.Status;

import java.time.LocalDateTime;

public record ListAllPollsDTO(
        Long id,
        String title,
        String question,
        Status status,
        LocalDateTime creationDate,
        LocalDateTime finishDate,
        Long userId
) {
    public ListAllPollsDTO(Poll poll) {
        this(
                poll.getId(),
                poll.getTitle(),
                poll.getQuestion(),
                poll.getStatus(),
                poll.getCreationDate(),
                poll.getFinishDate(),
                poll.getUsers().getId()
        );
    }
}
