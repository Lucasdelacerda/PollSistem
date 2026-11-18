package com.scrimet.polls.polls.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.services.PollOptionsService;
import com.scrimet.polls.polls.dtos.CreatePollDTO;
import com.scrimet.polls.polls.dtos.ListAllPollsDTO;
import com.scrimet.polls.polls.dtos.PollDTO;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.infrastructure.repositories.PollRepository;
import com.scrimet.polls.users.infrastructure.models.Users;
import com.scrimet.polls.users.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollsService {
    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final PollOptionsService pollOptionsService;

    @Transactional
    public Poll searchPollId(Long id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found"));
        closeIfExpired(poll, LocalDateTime.now());
        return poll;
    }

    @Transactional
    public PollDTO searchPollDTO(Long id) {
        return new PollDTO(searchPollId(id));
    }

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void closeExpiredPolls() {
        List<Poll> expiredPolls = pollRepository.findByStatusAndFinishDateLessThanEqual(
                Status.OPEN,
                LocalDateTime.now()
        );
        expiredPolls.forEach(poll -> poll.setStatus(Status.CLOSED));
    }

    private void closeIfExpired(Poll poll, LocalDateTime now) {
        if (poll.getStatus() == Status.OPEN
                && poll.getFinishDate() != null
                && !poll.getFinishDate().isAfter(now)) {
            poll.setStatus(Status.CLOSED);
        }
    }

    @Transactional
    public List<ListAllPollsDTO> findAllPolls() {
        List<Poll> polls = pollRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        polls.forEach(poll -> closeIfExpired(poll, now));
        return polls.stream().map(ListAllPollsDTO::new).toList();
    }

    @Transactional
    public List<ListAllPollsDTO> findByTitle(String title) {
        List<Poll> polls = pollRepository.findByTitleContainingIgnoreCase(title);
        LocalDateTime now = LocalDateTime.now();
        polls.forEach(poll -> closeIfExpired(poll, now));
        return polls.stream().map(ListAllPollsDTO::new).toList();
    }

    @Transactional
    public PollDTO createPoll(CreatePollDTO createPollDTO, Long userId) {
        if (createPollDTO.finishDate() == null || !createPollDTO.finishDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Finish date must be in the future");
        }
        Users creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Poll poll = new Poll();
        poll.setTitle(createPollDTO.title().trim());
        poll.setQuestion(createPollDTO.question().trim());
        poll.setFinishDate(createPollDTO.finishDate());
        poll.setStatus(Status.OPEN);
        poll.setUsers(creator);

        List<PollOptions> options = pollOptionsService.buildForPoll(poll, createPollDTO.options());
        poll.setOptions(options);

        return new PollDTO(pollRepository.save(poll));
    }

    @Transactional
    public PollDTO finishPoll(Long id, Long userId) {
        Poll poll = searchPollId(id);
        checkOwner(poll, userId, "finish the poll");
        if (poll.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A cancelled poll cannot be finished");
        }
        poll.setStatus(Status.CLOSED);
        return new PollDTO(poll);
    }

    @Transactional
    public PollDTO cancelPoll(Long id, Long userId) {
        Poll poll = searchPollId(id);
        checkOwner(poll, userId, "cancel the poll");
        if (poll.getStatus() == Status.CLOSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A finished poll cannot be cancelled");
        }
        poll.setStatus(Status.CANCELLED);
        return new PollDTO(poll);
    }

    @Transactional
    public void removePoll(Long id, Long userId) {
        Poll poll = searchPollId(id);
        checkOwner(poll, userId, "remove it");
        pollRepository.delete(poll);
    }

    private void checkOwner(Poll poll, Long userId, String action) {
        if (userId == null || poll.getUsers() == null || !poll.getUsers().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can " + action);
        }
    }
}