package com.scrimet.sistemaenquetes.Polls.services;


import com.scrimet.sistemaenquetes.Polls.dtos.CreatePollDTO;
import com.scrimet.sistemaenquetes.Polls.dtos.ListAllPollsDTO;
import com.scrimet.sistemaenquetes.Polls.dtos.PollDTO;
import com.scrimet.sistemaenquetes.Polls.enums.Status;
import com.scrimet.sistemaenquetes.Polls.infrainstructure.models.Poll;
import com.scrimet.sistemaenquetes.Polls.infrainstructure.repositories.PollRepository;


import com.scrimet.sistemaenquetes.Users.infraintructure.models.Users;
import com.scrimet.sistemaenquetes.Users.infraintructure.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollsService {
    private final PollRepository pollRepository;
    private final UserRepository userRepository;

    public Poll searchPollId(Long id){
        return pollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poll not found"));
    }
    public PollDTO searchPollDTO(Long id){return new PollDTO(searchPollId(id));}

    private void checkOwner(Poll poll,Long userId,String action){
        if(!poll.getUsers().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the creator can " + action);
        }
    }

    @Transactional(readOnly = true)
    public List<ListAllPollsDTO> findAllPolls(){
        List<Poll> polls = pollRepository.findAll();
        return polls.stream()
                .map(ListAllPollsDTO::new)
                .toList();
    }

    @Transactional
    public void removePoll(Long id, Long userId){
        Poll poll = searchPollId(id);
        if(!poll.getUsers().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the creator can remove it");
        }
        pollRepository.delete(poll);
    }
    @Transactional(readOnly = true)
    public List<ListAllPollsDTO> findByTitle(String title){
        List<Poll> poll = pollRepository.findByTitleContainingIgnoreCase(title);


        return poll.stream()
                .map(ListAllPollsDTO::new)
                .toList();
    }
    @Transactional
    public PollDTO statusPollEdit(Long id, Status newStatus, Long userId){
        Poll poll = searchPollId(id);
        checkOwner(poll,userId,"change the status");
        if(poll.getStatus() !=Status.OPEN){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Only the creator can alter status");
        }
        poll.setStatus(newStatus);
        return new PollDTO(poll);
    }
    @Transactional
    public PollDTO createPoll( CreatePollDTO createPollDTO, Long userId){

        User creator = (User) userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if(createPollDTO.finishDate().isBefore(LocalDateTime.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Finish date must be in the future");
        }
        Poll newPoll = new Poll();
        newPoll.setTitle(createPollDTO.title());
        newPoll.setQuestion(createPollDTO.question());
        newPoll.setFinishDate(createPollDTO.finishDate());
        newPoll.setStatus(Status.OPEN);
        newPoll.setUsers((Users) creator);
        Poll createdPoll = pollRepository.save(newPoll);
        return new PollDTO(createdPoll);
    }
}