package com.scrimet.polls.polls.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.infrastructure.repositories.PollOptionsRepository;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollResultsServiceTest {
    @Mock
    private PollsService pollsService;

    @Mock
    private PollOptionsRepository pollOptionsRepository;

    @InjectMocks
    private PollResultsService pollResultsService;

    @Test
    void returnsDescendingRankingPercentagesAndTiedWinners() {
        Poll poll = new Poll();
        poll.setId(3L);
        poll.setTitle("Poll");
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().plusHours(1));
        when(pollsService.searchPollId(poll.getId())).thenReturn(poll);
        when(pollOptionsRepository.findByPoll_Id(poll.getId())).thenReturn(List.of(
                option(1L, "Second", 2L),
                option(2L, "First", 3L),
                option(3L, "Tied winner", 3L)
        ));

        var results = pollResultsService.getResults(poll.getId());

        assertEquals(8L, results.totalVotes());
        assertEquals(3L, results.ranking().get(0).votes());
        assertEquals(3L, results.ranking().get(1).votes());
        assertEquals(2L, results.ranking().get(2).votes());
        assertEquals(2, results.winners().size());
        assertEquals(37.5, results.winners().get(0).percentage().doubleValue());
        assertEquals(1, results.winners().get(0).rank());
        assertEquals(1, results.winners().get(1).rank());
    }

    private PollOptions option(Long id, String text, Long votes) {
        PollOptions option = new PollOptions();
        option.setId(id);
        option.setText(text);
        option.setPollQuantity(votes);
        return option;
    }
}
