package com.scrimet.polls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PollsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PollsApplication.class, args);
    }

}
