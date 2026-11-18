package com.scrimet.polls.polls.services;

import com.scrimet.polls.polls.dtos.CreatePollDTO;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.infrastructure.repositories.PollRepository;
import com.scrimet.polls.users.infrastructure.models.Users;
import com.scrimet.polls.users.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollsServiceTest {
    @Mock
    private PollRepository pollRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PollsService pollsService;

    @Test
    void rejectsPollWithFinishDateThatIsNotInTheFuture() {
        Long userId = 1L;
        CreatePollDTO createPollDTO = new CreatePollDTO(
                "Title",
                "Question",
                LocalDateTime.now().minusSeconds(1),
                List.of("First", "Second")
        );

        assertThrows(ResponseStatusException.class, () -> pollsService.createPoll(createPollDTO, userId));
    }

    @Test
    void closesExpiredOpenPolls() {
        Poll poll = new Poll();
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().minusSeconds(1));
        when(pollRepository.findByStatusAndFinishDateLessThanEqual(eq(Status.OPEN), any(LocalDateTime.class)))
                .thenReturn(List.of(poll));

        pollsService.closeExpiredPolls();

        assertEquals(Status.CLOSED, poll.getStatus());
    }

    @Test
    void allowsCreatorToClosePoll() {
        Long userId = 1L;
        Users creator = new Users();
        creator.setId(userId);
        Poll poll = new Poll();
        poll.setId(2L);
        poll.setUsers(creator);
        poll.setStatus(Status.OPEN);
        poll.setFinishDate(LocalDateTime.now().plusHours(1));
        when(pollRepository.findById(poll.getId())).thenReturn(Optional.of(poll));

        pollsService.finishPoll(poll.getId(), userId);

        assertEquals(Status.CLOSED, poll.getStatus());
    }
}
