package com.scrimet.sistemaenquetes.Polls.infrainstructure.repositories;


import com.scrimet.sistemaenquetes.Polls.infrainstructure.models.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {

    List<Poll> findByTitleContainingIgnoreCase(String title);
}
