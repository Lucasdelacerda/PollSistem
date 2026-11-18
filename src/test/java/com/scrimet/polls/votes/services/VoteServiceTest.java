package com.scrimet.polls.votes.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.infrastructure.repositories.PollOptionsRepository;
import com.scrimet.polls.polls.dtos.PollResultsDTO;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.services.PollResultsService;
import com.scrimet.polls.polls.services.PollsService;
import com.scrimet.polls.users.infrastructure.models.Users;
import com.scrimet.polls.users.infrastructure.repositories.UserRepository;
import com.scrimet.polls.votes.dtos.VoteRequest;
import com.scrimet.polls.votes.repositories.VoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {
    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PollsService pollsService;

    @Mock
    private PollOptionsRepository pollOptionsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PollResultsService pollResultsService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private VoteService voteService;

    @Test
    void savesVoteAndIncrementsOptionCount() {
        Poll poll = openPoll(4L);
        Users user = new Users();
        user.setId(8L);
        PollOptions option = new PollOptions();
        option.setId(12L);
        option.setPoll(poll);
        when(pollsService.searchPollId(poll.getId())).thenReturn(poll);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pollOptionsRepository.findById(option.getId())).thenReturn(Optional.of(option));
        when(voteRepository.existsByPoll_IdAndUser_Id(poll.getId(), user.getId())).thenReturn(false);
        when(pollOptionsRepository.incrementPollQuantity(option.getId())).thenReturn(1);
        when(pollResultsService.getResults(poll.getId()))
                .thenReturn(new PollResultsDTO(poll.getId(), "Title", Status.OPEN, 1, java.util.List.of(), java.util.List.of()));

        voteService.castVote(poll.getId(), new VoteRequest(user.getId(), option.getId()));

        verify(voteRepository).saveAndFlush(any());
        verify(pollOptionsRepository).incrementPollQuantity(option.getId());
    }

    @Test
    void rejectsSecondVoteBySameUserInPoll() {
        Poll poll = openPoll(4L);
        Users user = new Users();
        user.setId(8L);
        PollOptions option = new PollOptions();
        option.setId(12L);
        option.setPoll(poll);
        when(pollsService.searchPollId(poll.getId())).thenReturn(poll);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(pollOptionsRepository.findById(option.getId())).thenReturn(Optional.of(option));
        when(voteRepository.existsByPoll_IdAndUser_Id(poll.getId(), user.getId())).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> voteService.castVote(poll.getId(), new VoteRequest(user.getId(), option.getId())));

        verify(voteRepository, never()).saveAndFlush(any());
        verify(pollOptionsRepository, never()).incrementPollQuantity(option.getId());
    }

    private Poll openPoll(Long id) {
        Poll poll = new Poll();
        poll.setId(id);
        poll.setTitle("Title");
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().plusHours(1));
        return poll;
    }
}
