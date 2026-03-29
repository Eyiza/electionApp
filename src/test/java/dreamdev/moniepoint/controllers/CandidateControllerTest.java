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

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test successful candidate creation")
    void addCandidateSuccess() {
        CandidateRequest candidateRequest = new CandidateRequest();
        candidateRequest.setFirstName("John");
        candidateRequest.setLastName("Doe");
        candidateRequest.setPosition("President");

        CandidateCreationResponse candidateResponse = new CandidateCreationResponse();
        candidateResponse.setFirstName("John");
        candidateResponse.setLastName("Doe");
        candidateResponse.setPosition("President");

        ApiResponse response = new ApiResponse(true, candidateResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test duplicate candidate creation fails")
    void addDuplicateCandidateFails() {
        CandidateRequest candidateRequest = new CandidateRequest();
        candidateRequest.setFirstName("John");
        candidateRequest.setLastName("Doe");
        candidateRequest.setPosition("President");

        CandidateCreationResponse candidateResponse = new CandidateCreationResponse();
        candidateResponse.setFirstName("John");
        candidateResponse.setLastName("Doe");
        candidateResponse.setPosition("President");

        ApiResponse response = new ApiResponse(true, candidateResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all candidates returns list")
    void getAllCandidates_listIs2Test() {
        CandidateRequest candidateRequest = new CandidateRequest();
        candidateRequest.setFirstName("John");
        candidateRequest.setLastName("Doe");
        candidateRequest.setPosition("President");

        CandidateCreationResponse candidateResponse = new CandidateCreationResponse();
        candidateResponse.setFirstName("John");
        candidateResponse.setLastName("Doe");
        candidateResponse.setPosition("President");

        ApiResponse response = new ApiResponse(true, candidateResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateRequest)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));

        candidateRequest.setFirstName("Precious");
        candidateRequest.setLastName("Michael");
        candidateRequest.setPosition("President");

        candidateResponse.setFirstName("Precious");
        candidateResponse.setLastName("Michael");
        candidateResponse.setPosition("President");

        response = new ApiResponse(true, candidateResponse);

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateRequest)
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
                .isCreated()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }




}