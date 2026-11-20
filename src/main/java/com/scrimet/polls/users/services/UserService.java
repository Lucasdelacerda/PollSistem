package com.scrimet.polls.users.services;


import com.scrimet.polls.users.dtos.CreateUserDTO;
import com.scrimet.polls.users.dtos.LoginDTO;
import com.scrimet.polls.users.dtos.UserDTO;
import com.scrimet.polls.users.infrastructure.models.Users;
import com.scrimet.polls.users.infrastructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional(readOnly = true)
    public Users searchUserId(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO){
        if(userRepository.findByEmail(createUserDTO.email()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Users user = new Users();
        user.setEmail(createUserDTO.email());
        user.setName(createUserDTO.name());
        user.setPasswordHash(passwordEncoder.encode(createUserDTO.password()));
        try {
            return new UserDTO(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMostSpecificCause() instanceof ConstraintViolationException violation
                    && "uk_users_email".equalsIgnoreCase(violation.getConstraintName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered", exception);
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public UserDTO login(LoginDTO loginDTO) {
        Users user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(loginDTO.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new UserDTO(user);
    }
}
