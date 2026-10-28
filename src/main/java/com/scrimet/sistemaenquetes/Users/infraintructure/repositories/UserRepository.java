package com.scrimet.sistemaenquetes.Users.infraintructure.repositories;

import com.scrimet.sistemaenquetes.Users.infraintructure.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
}
