package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.dtos.responses.CandidateCreationResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.*;

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

    private CandidateCreationResponse candidatePreciousResponse;
    private CandidateCreationResponse candidateJohnResponse;

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

        candidatePreciousResponse = new CandidateCreationResponse();
        candidatePreciousResponse.setFirstName("Precious");
        candidatePreciousResponse.setLastName("Michael");
        candidatePreciousResponse.setPosition("President");

        candidateJohnResponse = new CandidateCreationResponse();
        candidateJohnResponse.setFirstName("John");
        candidateJohnResponse.setLastName("Doe");
        candidateJohnResponse.setPosition("President");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test successful candidate creation")
    void addCandidateSuccess() {
        ApiResponse response = new ApiResponse(true, candidateJohnResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test duplicate candidate creation fails")
    void addDuplicateCandidateFails() {
        ApiResponse response = new ApiResponse(true, candidateJohnResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));

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
        ApiResponse response = new ApiResponse(true, candidateJohnResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));

        response = new ApiResponse(true, candidatePreciousResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidatePrecious)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));

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

        restTestClient.post()
                .uri(url("/candidate"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }




}