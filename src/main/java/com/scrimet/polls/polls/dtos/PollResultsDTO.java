package com.scrimet.polls.polls.dtos;

import com.scrimet.polls.polls.enums.Status;

import java.util.List;

public record PollResultsDTO(
        Long pollId,
        String title,
        Status status,
        long totalVotes,
        List<PollResultOptionDTO> ranking,
        List<PollResultOptionDTO> winners
) {
}
