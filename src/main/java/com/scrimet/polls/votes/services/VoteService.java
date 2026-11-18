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
import com.scrimet.polls.votes.events.PollVoteRecordedEvent;
import com.scrimet.polls.votes.models.Vote;
import com.scrimet.polls.votes.repositories.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final PollsService pollsService;
    private final PollOptionsRepository pollOptionsRepository;
    private final UserRepository userRepository;
    private final PollResultsService pollResultsService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PollResultsDTO castVote(Long pollId, VoteRequest request) {
        Poll poll = pollsService.searchPollId(pollId);
        if (poll.getStatus() != Status.OPEN || !poll.getFinishDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Poll is not open for voting");
        }

        Users user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        PollOptions option = pollOptionsRepository.findById(request.optionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll option not found"));
        if (!option.getPoll().getId().equals(pollId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option does not belong to this poll");
        }
        if (voteRepository.existsByPoll_IdAndUser_Id(pollId, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already voted in this poll");
        }

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setOption(option);
        vote.setUser(user);
        try {
            voteRepository.saveAndFlush(vote);
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMostSpecificCause() instanceof ConstraintViolationException violation
                    && "uk_votes_poll_user".equalsIgnoreCase(violation.getConstraintName())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "User has already voted in this poll",
                        exception
                );
            }
            throw exception;
        }

        if (pollOptionsRepository.incrementPollQuantity(option.getId()) != 1) {
            throw new IllegalStateException("Could not update poll option vote count");
        }

        eventPublisher.publishEvent(new PollVoteRecordedEvent(pollId));
        return pollResultsService.getResults(pollId);
    }
}
