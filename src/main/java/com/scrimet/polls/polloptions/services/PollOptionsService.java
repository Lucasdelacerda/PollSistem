package com.scrimet.polls.polloptions.services;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polloptions.infrastructure.repositories.PollOptionsRepository;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.infrastructure.repositories.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollOptionsService {
    private final PollOptionsRepository pollOptionsRepository;
    private final PollRepository pollRepository;

    @Transactional(readOnly = true)
    public List<PollOptions> findByPollId(Long pollId) {
        if (!pollRepository.existsById(pollId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found");
        }
        return pollOptionsRepository.findByPoll_Id(pollId);
    }

    @Transactional
    public PollOptions createOption(Long pollId, String text) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found"));
        ensureOpen(poll);
        return pollOptionsRepository.save(buildOption(poll, text));
    }

    public List<PollOptions> buildForPoll(Poll poll, List<String> texts) {
        if (texts == null || texts.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A poll must have at least two options");
        }
        List<PollOptions> options = new ArrayList<>();
        for (String text : texts) {
            options.add(buildOption(poll, text));
        }
        return options;
    }

    private PollOptions buildOption(Poll poll, String text) {
        if (text == null || text.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Option text is required");
        }
        PollOptions option = new PollOptions();
        option.setText(text.trim());
        option.setPollQuantity(0L);
        option.setPoll(poll);
        return option;
    }

    private void ensureOpen(Poll poll) {
        if (poll.getStatus() != Status.OPEN
                || poll.getFinishDate() == null
                || !poll.getFinishDate().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot add options to a closed poll");
        }
    }
}
