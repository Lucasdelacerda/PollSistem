package com.scrimet.polls.polls;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.scrimet.polls.polls.controller.PollsController;
import com.scrimet.polls.polls.services.PollResultsService;
import com.scrimet.polls.polls.services.PollResultsStreamService;
import com.scrimet.polls.polls.services.PollsService;
import com.scrimet.polls.votes.services.VoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PollFlowIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsUsersAndValidatesUserRequests() throws Exception {
        String email = uniqueEmail();
        String requestBody = objectMapper.writeValueAsString(Map.of("name", "Voter", "email", email));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Voter"))
                .andExpect(jsonPath("$.email").value(email));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Invalid", "email", "not-an-email"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createsListsAndRetrievesPolls() throws Exception {
        long ownerId = createUser("owner");
        String title = "Poll-" + UUID.randomUUID();
        CreatedPoll poll = createPoll(ownerId, title);

        MvcResult listResult = mockMvc.perform(get("/polls"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode polls = objectMapper.readTree(listResult.getResponse().getContentAsString());
        boolean found = false;
        for (JsonNode item : polls) {
            if (title.equals(item.get("title").asText())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "The created poll should appear in the poll list");

        mockMvc.perform(get("/poll/{pollId}", poll.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.options.length()").value(2));
    }

    @Test
    void rejectsInvalidPollCreationRequests() throws Exception {
        long ownerId = createUser("creator");
        String validFinishDate = LocalDateTime.now().plusDays(1).withNano(0).toString();

        mockMvc.perform(post("/poll")
                        .param("userId", Long.toString(ownerId + 999_999))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pollRequest("Unknown owner", validFinishDate, List.of("First", "Second"))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/poll")
                        .param("userId", Long.toString(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pollRequest("Expired", LocalDateTime.now().minusHours(1).toString(),
                                List.of("First", "Second"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/poll")
                        .param("userId", Long.toString(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pollRequest("Too few options", validFinishDate, List.of("Only one"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void votesOnceAndReturnsUpdatedRankedResults() throws Exception {
        long ownerId = createUser("owner");
        long firstVoterId = createUser("first-voter");
        long secondVoterId = createUser("second-voter");
        CreatedPoll poll = createPoll(ownerId, "Voting-" + UUID.randomUUID());

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(firstVoterId, poll.firstOptionId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotes").value(1));

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(firstVoterId, poll.secondOptionId())))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(secondVoterId, poll.secondOptionId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotes").value(2));

        MvcResult resultsResult = mockMvc.perform(get("/polls/{pollId}/results", poll.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ranking[0].votes").value(1))
                .andExpect(jsonPath("$.ranking[0].rank").value(1))
                .andExpect(jsonPath("$.ranking[1].rank").value(1))
                .andExpect(jsonPath("$.winners.length()").value(2))
                .andReturn();
        JsonNode results = objectMapper.readTree(resultsResult.getResponse().getContentAsString());
        assertEquals(2, results.get("totalVotes").asInt());
        assertEquals(50.0, results.get("ranking").get(0).get("percentage").doubleValue());
    }

    @Test
    void rejectsVotesForMissingUsersOptionsOrAnotherPoll() throws Exception {
        long ownerId = createUser("owner");
        long voterId = createUser("voter");
        CreatedPoll poll = createPoll(ownerId, "Vote validation-" + UUID.randomUUID());
        CreatedPoll otherPoll = createPoll(ownerId, "Other poll-" + UUID.randomUUID());

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(voterId + 999_999, poll.firstOptionId())))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(voterId, poll.firstOptionId() + 999_999)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/polls/{pollId}/vote", poll.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(voterId, otherPoll.firstOptionId())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void finishesCancelsAndDeletesPolls() throws Exception {
        long ownerId = createUser("owner");
        long otherUserId = createUser("other-user");
        CreatedPoll pollToFinish = createPoll(ownerId, "Finish-" + UUID.randomUUID());

        mockMvc.perform(patch("/polls/{pollId}/finish", pollToFinish.id())
                        .param("userId", Long.toString(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));

        mockMvc.perform(patch("/polls/{pollId}/finish", pollToFinish.id())
                        .param("userId", Long.toString(otherUserId)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/polls/{pollId}/vote", pollToFinish.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(otherUserId, pollToFinish.firstOptionId())))
                .andExpect(status().isConflict());

        CreatedPoll pollToCancel = createPoll(ownerId, "Cancel-" + UUID.randomUUID());
        mockMvc.perform(patch("/polls/{pollId}/cancel", pollToCancel.id())
                        .param("userId", Long.toString(ownerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(post("/polls/{pollId}/vote", pollToCancel.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest(otherUserId, pollToCancel.firstOptionId())))
                .andExpect(status().isConflict());

        CreatedPoll pollToDelete = createPoll(ownerId, "Delete-" + UUID.randomUUID());
        mockMvc.perform(delete("/poll/{pollId}", pollToDelete.id())
                        .param("userId", Long.toString(ownerId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/poll/{pollId}", pollToDelete.id()))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/polls/{pollId}/results", pollToDelete.id()))
                .andExpect(status().isNotFound());
    }

    private long createUser(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", name, "email", uniqueEmail()))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private CreatedPoll createPoll(long ownerId, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/poll")
                        .param("userId", Long.toString(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pollRequest(
                                title,
                                LocalDateTime.now().plusDays(1).withNano(0).toString(),
                                List.of("First", "Second")
                        )))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode poll = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals("OPEN", poll.get("status").asText());
        return new CreatedPoll(
                poll.get("id").asLong(),
                poll.get("options").get(0).get("id").asLong(),
                poll.get("options").get(1).get("id").asLong()
        );
    }

    private String pollRequest(String title, String finishDate, List<String> options) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "title", title,
                "question", "Choose one",
                "finishDate", finishDate,
                "options", options
        ));
    }

    private String voteRequest(long userId, long optionId) throws Exception {
        return objectMapper.writeValueAsString(Map.of("userId", userId, "optionId", optionId));
    }

    private String uniqueEmail() {
        return UUID.randomUUID() + "@example.test";
    }

    private record CreatedPoll(long id, long firstOptionId, long secondOptionId) {
    }

    @Test
    void mapsTheServerSentEventsEndpoint() throws Exception {
        PollsService pollsService = mock(PollsService.class);
        VoteService voteService = mock(VoteService.class);
        PollResultsService pollResultsService = mock(PollResultsService.class);
        PollResultsStreamService streamService = mock(PollResultsStreamService.class);
        SseEmitter emitter = new SseEmitter();
        when(streamService.subscribe(7L)).thenReturn(emitter);

        MockMvc sseMockMvc = MockMvcBuilders.standaloneSetup(
                new PollsController(pollsService, voteService, pollResultsService, streamService)
        ).build();

        MvcResult result = sseMockMvc.perform(get("/polls/{pollId}/results", 7L)
                        .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(request().asyncStarted())
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        verify(streamService).subscribe(7L);
        emitter.complete();
    }
}
