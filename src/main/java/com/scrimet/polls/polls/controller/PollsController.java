package com.scrimet.polls.polls.controller;

import com.scrimet.polls.polls.dtos.CreatePollDTO;
import com.scrimet.polls.polls.dtos.ListAllPollsDTO;
import com.scrimet.polls.polls.dtos.PollDTO;
import com.scrimet.polls.polls.dtos.PollResultsDTO;
import com.scrimet.polls.polls.services.PollResultsService;
import com.scrimet.polls.polls.services.PollResultsStreamService;
import com.scrimet.polls.polls.services.PollsService;
import com.scrimet.polls.votes.dtos.VoteRequest;
import com.scrimet.polls.votes.services.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PollsController {
    private final PollsService pollsService;
    private final VoteService voteService;
    private final PollResultsService pollResultsService;
    private final PollResultsStreamService pollResultsStreamService;

    @GetMapping("/polls")
    public List<ListAllPollsDTO> findAllPolls() {
        return pollsService.findAllPolls();
    }

    @GetMapping("/poll/{pollId}")
    public PollDTO findPollById(@PathVariable Long pollId) {
        return pollsService.searchPollDTO(pollId);
    }

    @PostMapping("/poll")
    @ResponseStatus(HttpStatus.CREATED)
    public PollDTO createPoll(
            @Valid @RequestBody CreatePollDTO createPollDTO,
            @RequestParam Long userId
    ) {
        return pollsService.createPoll(createPollDTO, userId);
    }

    @PostMapping("/polls/{pollId}/vote")
    public PollResultsDTO castVote(
            @PathVariable Long pollId,
            @Valid @RequestBody VoteRequest request
    ) {
        return voteService.castVote(pollId, request);
    }

    @GetMapping(value = "/polls/{pollId}/results", produces = MediaType.APPLICATION_JSON_VALUE)
    public PollResultsDTO getResults(@PathVariable Long pollId) {
        return pollResultsService.getResults(pollId);
    }

    @GetMapping(
            value = "/polls/{pollId}/results",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            headers = "Accept=text/event-stream"
    )
    public SseEmitter streamResults(@PathVariable Long pollId) {
        return pollResultsStreamService.subscribe(pollId);
    }

    @PatchMapping("/polls/{pollId}/finish")
    public PollDTO finishPoll(@PathVariable Long pollId, @RequestParam Long userId) {
        return pollsService.finishPoll(pollId, userId);
    }

    @PatchMapping("/polls/{pollId}/cancel")
    public PollDTO cancelPoll(@PathVariable Long pollId, @RequestParam Long userId) {
        return pollsService.cancelPoll(pollId, userId);
    }

    @DeleteMapping("/poll/{pollId}")
    public ResponseEntity<Void> removePoll(@PathVariable Long pollId, @RequestParam Long userId) {
        pollsService.removePoll(pollId, userId);
        return ResponseEntity.noContent().build();
    }
}
