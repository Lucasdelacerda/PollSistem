package com.scrimet.polls.polls.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.infrastructure.repositories.PollOptionsRepository;
import com.scrimet.polls.polls.dtos.PollResultOptionDTO;
import com.scrimet.polls.polls.dtos.PollResultsDTO;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollResultsService {
    private final PollsService pollsService;
    private final PollOptionsRepository pollOptionsRepository;

    @Transactional
    public PollResultsDTO getResults(Long pollId) {
        Poll poll = pollsService.searchPollId(pollId);
        List<PollOptions> options = pollOptionsRepository.findByPoll_Id(pollId);
        long totalVotes = options.stream().mapToLong(this::voteCount).sum();

        List<PollOptions> rankedOptions = options.stream()
                .sorted(Comparator.comparingLong(this::voteCount)
                        .reversed()
                        .thenComparing(PollOptions::getId))
                .toList();

        List<PollResultOptionDTO> ranking = new ArrayList<>();
        long previousVoteCount = Long.MIN_VALUE;
        int currentRank = 0;
        for (int index = 0; index < rankedOptions.size(); index++) {
            PollOptions option = rankedOptions.get(index);
            long votes = voteCount(option);
            if (votes != previousVoteCount) {
                currentRank = index + 1;
                previousVoteCount = votes;
            }
            BigDecimal percentage = totalVotes == 0
                    ? BigDecimal.ZERO.setScale(2)
                    : BigDecimal.valueOf(votes)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(totalVotes), 2, RoundingMode.HALF_UP);
            ranking.add(new PollResultOptionDTO(option.getId(), option.getText(), votes, percentage, currentRank));
        }

        long highestVotes = rankedOptions.isEmpty() ? 0L : voteCount(rankedOptions.get(0));
        List<PollResultOptionDTO> winners = highestVotes == 0
                ? List.of()
                : ranking.stream().filter(option -> option.votes() == highestVotes).toList();

        return new PollResultsDTO(poll.getId(), poll.getTitle(), poll.getStatus(), totalVotes, ranking, winners);
    }

    private long voteCount(PollOptions option) {
        return option.getPollQuantity() == null ? 0L : option.getPollQuantity();
    }
}
