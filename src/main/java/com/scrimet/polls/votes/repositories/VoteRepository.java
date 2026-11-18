package com.scrimet.polls.votes.repositories;

import com.scrimet.polls.votes.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPoll_IdAndUser_Id(Long pollId, Long userId);
}
