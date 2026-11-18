package com.scrimet.polls.polls.dtos;

import com.scrimet.polls.polls.enums.Status;

public record PollUpdate(
        Long userId,
        Status status
) {
}
