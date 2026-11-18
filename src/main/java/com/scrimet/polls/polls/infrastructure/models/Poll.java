package com.scrimet.polls.polls.infrastructure.models;

import com.scrimet.polls.polloptions.infrastructure.models.PollOptions;
import com.scrimet.polls.polls.enums.Status;
import com.scrimet.polls.votes.models.Vote;
import com.scrimet.polls.users.infrastructure.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
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
    private Status status = Status.OPEN;

    @CreationTimestamp
    @Column(nullable = false,updatable = false,name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "finish_date", nullable = false)
    private LocalDateTime finishDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private Users users;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOptions> options = new ArrayList<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}
