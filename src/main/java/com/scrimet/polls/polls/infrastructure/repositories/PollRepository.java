package com.scrimet.polls.polls.infrastructure.repositories;


import com.scrimet.polls.polls.infrastructure.models.Poll;
import com.scrimet.polls.polls.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findByTitleContainingIgnoreCase(String title);

    List<Poll> findByStatusAndFinishDateLessThanEqual(Status status, LocalDateTime finishDate);
}
