package com.scrimet.polls.users.infrastructure.repositories;

import com.scrimet.polls.users.infrastructure.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
