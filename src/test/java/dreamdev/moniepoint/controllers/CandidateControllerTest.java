package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class CandidateControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private CandidateRepository candidateRepository;

    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

    private CandidateResponse candidatePreciousResponse;
    private CandidateResponse candidateJohnResponse;

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();

        candidatePrecious = new CandidateRequest();
        candidatePrecious.setFirstName("Precious");
        candidatePrecious.setLastName("Michael");
        candidatePrecious.setPosition("President");

        candidateJohn = new CandidateRequest();
        candidateJohn.setFirstName("John");
        candidateJohn.setLastName("Doe");
        candidateJohn.setPosition("President");

        candidatePreciousResponse = new CandidateResponse();
        candidatePreciousResponse.setFirstName("Precious");
        candidatePreciousResponse.setLastName("Michael");
        candidatePreciousResponse.setPosition("President");

        candidateJohnResponse = new CandidateResponse();
        candidateJohnResponse.setFirstName("John");
        candidateJohnResponse.setLastName("Doe");
        candidateJohnResponse.setPosition("President");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test successful candidate creation")
    void addCandidateSuccess() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
//                .json(objectMapper.writeValueAsString(response));
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();
    }

    @Test
    @DisplayName("Test duplicate candidate creation fails")
    void addDuplicateCandidateFails() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all candidates returns list")
    void getAllCandidates_listIs2Test() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidatePrecious)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("Precious")
                .jsonPath("$.data.lastName").isEqualTo("Michael")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();

        restTestClient.get()
                .uri(url("/candidates"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test get all candidates returns empty list")
    void getAllCandidates_isEmptyTest() {
        ApiResponse response = new ApiResponse(true, new ArrayList<>());

        restTestClient.get()
                .uri(url("/candidates"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test get candidate by name and position and it succeeds")
    void getCandidateByName_isSuccessTest() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();

        restTestClient.get()
                .uri(url("/candidates/search?position=President&firstName=John&lastName=Doe"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[0].lastName").isEqualTo("Doe")
                .jsonPath("$.data[0].position").isEqualTo("President")
                .jsonPath("$.data[0].voteCount").isEqualTo(0);
    }

    @Test
    @DisplayName("Search candidates with incomplete fields should return similar matches")
    void searchCandidates_byIncompleteFieldsTest() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.position").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0)
                .jsonPath("$.data.id").exists();

        restTestClient.get()
                .uri(url("/candidates/search?firstName=jo&lastName=d"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[0].lastName").isEqualTo("Doe")
                .jsonPath("$.data[0].position").isEqualTo("President")
                .jsonPath("$.data[0].voteCount").isEqualTo(0);
    }

}