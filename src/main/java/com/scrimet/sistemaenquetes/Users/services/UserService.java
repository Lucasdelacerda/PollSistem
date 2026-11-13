package com.scrimet.sistemaenquetes.Users.services;


import com.scrimet.sistemaenquetes.Users.dtos.CreateUserDTO;
import com.scrimet.sistemaenquetes.Users.infraintructure.models.Users;
import com.scrimet.sistemaenquetes.Users.infraintructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public Users searchUserId(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    //
    public void createUser(CreateUserDTO createUserDTO){
        if(userRepository.findByEmail(createUserDTO.email()).isPresent()){
            throw new RuntimeException("Usuario existente");
        }

        Users user = new Users();
        user.setEmail(createUserDTO.email());
        user.setName(createUserDTO.name());
        userRepository.save(user);

    }
}
