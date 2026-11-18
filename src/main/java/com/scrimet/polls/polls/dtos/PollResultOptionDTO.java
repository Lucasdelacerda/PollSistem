package com.scrimet.polls.polls.dtos;

import java.math.BigDecimal;

public record PollResultOptionDTO(
        Long optionId,
        String text,
        Long votes,
        BigDecimal percentage,
        int rank
) {
}
