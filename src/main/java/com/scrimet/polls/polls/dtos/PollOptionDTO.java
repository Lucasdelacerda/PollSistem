package com.scrimet.polls.polls.dtos;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;

public record PollOptionDTO(Long id, String text, Long pollQuantity) {
    public PollOptionDTO(PollOptions option) {
        this(option.getId(), option.getText(), option.getPollQuantity());
    }
}
