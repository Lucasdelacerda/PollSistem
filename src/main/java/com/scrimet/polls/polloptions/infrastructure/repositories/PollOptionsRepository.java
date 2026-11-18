package com.scrimet.polls.polloptions.infrastructure.repositories;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PollOptionsRepository extends JpaRepository<PollOptions, Long> {
    List<PollOptions> findByPoll_Id(Long pollId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PollOptions po set po.pollQuantity = po.pollQuantity + 1 where po.id = :optionId")
    int incrementPollQuantity(@Param("optionId") Long optionId);
}
