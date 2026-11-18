package com.scrimet.polls.polls.services;

import com.scrimet.polls.polls.dtos.PollResultsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PollResultsStreamService {
    private static final long STREAM_TIMEOUT_MILLISECONDS = 30 * 60 * 1000L;

    private final PollResultsService pollResultsService;
    private final ConcurrentHashMap<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long pollId) {
        PollResultsDTO currentResults = pollResultsService.getResults(pollId);
        SseEmitter emitter = new SseEmitter(STREAM_TIMEOUT_MILLISECONDS);
        Set<SseEmitter> pollEmitters = emitters.computeIfAbsent(
                pollId,
                ignored -> ConcurrentHashMap.newKeySet()
        );
        pollEmitters.add(emitter);
        emitter.onCompletion(() -> removeEmitter(pollId, emitter));
        emitter.onTimeout(() -> removeEmitter(pollId, emitter));
        emitter.onError(error -> removeEmitter(pollId, emitter));
        sendResults(emitter, currentResults);
        return emitter;
    }

    public void publish(PollResultsDTO results) {
        Set<SseEmitter> pollEmitters = emitters.get(results.pollId());
        if (pollEmitters == null) {
            return;
        }
        for (SseEmitter emitter : pollEmitters) {
            sendResults(emitter, results);
        }
    }

    private void sendResults(SseEmitter emitter, PollResultsDTO results) {
        try {
            emitter.send(SseEmitter.event().name("results").data(results));
        } catch (IOException | IllegalStateException exception) {
            emitter.completeWithError(exception);
        }
    }

    private void removeEmitter(Long pollId, SseEmitter emitter) {
        Set<SseEmitter> pollEmitters = emitters.get(pollId);
        if (pollEmitters != null) {
            pollEmitters.remove(emitter);
            if (pollEmitters.isEmpty()) {
                emitters.remove(pollId, pollEmitters);
            }
        }
    }
}
