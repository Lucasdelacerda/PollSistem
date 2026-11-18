package com.scrimet.polls.votes.dtos;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull Long userId,
        @NotNull Long optionId
) {
}
