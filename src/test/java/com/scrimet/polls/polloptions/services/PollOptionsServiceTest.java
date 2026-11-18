package com.scrimet.polls.polloptions.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.infrastructure.repositories.PollOptionsRepository;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.infrastructure.repositories.PollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollOptionsServiceTest {
    @Mock
    private PollOptionsRepository pollOptionsRepository;

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private PollOptionsService pollOptionsService;

    @Test
    void createsOptionWithZeroVotesForAnOpenPoll() {
        Poll poll = new Poll();
        poll.setId(1L);
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().plusHours(1));
        when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));
        when(pollOptionsRepository.save(any(PollOptions.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PollOptions option = pollOptionsService.createOption(poll.getId(), "Choice");

        assertEquals("Choice", option.getText());
        assertEquals(0L, option.getPollQuantity());
        assertEquals(poll, option.getPoll());
    }

    @Test
    void rejectsCreatingOptionForExpiredPoll() {
        Poll poll = new Poll();
        poll.setId(1L);
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().minusSeconds(1));
        when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));

        assertThrows(ResponseStatusException.class,
                () -> pollOptionsService.createOption(poll.getId(), "Choice"));
    }
}
