package com.scrimet.polls.polls.dtos;

import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record PollDTO(
        Long id,
        String title,
        String question,
        Status status,
        Long userId,
        LocalDateTime finishDate,
        LocalDateTime creationDate,
        List<PollOptionDTO> options
) {
    public PollDTO(Poll poll) {
        this(
                poll.getId(),
                poll.getTitle(),
                poll.getQuestion(),
                poll.getStatus(),
                poll.getUsers().getId(),
                poll.getFinishDate(),
                poll.getCreationDate(),
                poll.getOptions().stream().map(PollOptionDTO::new).toList()
        );
    }
}
