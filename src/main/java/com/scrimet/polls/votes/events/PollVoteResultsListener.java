package com.scrimet.polls.votes.events;

import com.scrimet.polls.polls.services.PollResultsService;
import com.scrimet.polls.polls.services.PollResultsStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PollVoteResultsListener {
    private final PollResultsService pollResultsService;
    private final PollResultsStreamService pollResultsStreamService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onVoteRecorded(PollVoteRecordedEvent event) {
        pollResultsStreamService.publish(pollResultsService.getResults(event.pollId()));
    }
}
