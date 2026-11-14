package com.scrimet.sistemaenquetes.Polls.infrainstructure.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scrimet.sistemaenquetes.Polls.enums.Status;
import com.scrimet.sistemaenquetes.Users.infraintructure.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="enquetes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String question;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status =  Status.ABERTA;
    @CreationTimestamp
    @Column(nullable = false,updatable = false,name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "finish_date")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime finishDate;

    @ManyToOne
    @JoinColumn(nullable = false,name = "user_id")
    private Users users;
}
