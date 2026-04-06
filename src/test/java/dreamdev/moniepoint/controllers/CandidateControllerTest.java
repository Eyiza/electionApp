package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.services.CandidateService;
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

    @Autowired
    private CandidateService candidateService;

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


    @Test
    @DisplayName("Test that get results returns empty when there are no candidates")
    void getResults_emptyTest() {
        restTestClient.get()
                .uri(url("/results"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isEmpty();
    }

    @Test
    @DisplayName("Test that get results groups candidates by position")
    void getResults_groupedByPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        CandidateRequest candidateJane = new CandidateRequest();
        candidateJane.setFirstName("Jane");
        candidateJane.setLastName("Doe");
        candidateJane.setPosition("Governor");
        candidateService.createCandidate(candidateJane);

        restTestClient.get()
                .uri(url("/results"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.President.length()").isEqualTo(2)
                .jsonPath("$.data.Governor.length()").isEqualTo(1);
    }

    @Test
    @DisplayName("Test that get results orders candidates by descending vote count")
    void getResults_orderedByVoteCount_Test() {
        String preciousId = candidateService.createCandidate(candidatePrecious).getId();
        String johnId = candidateService.createCandidate(candidateJohn).getId();

        Candidate candidate = candidateRepository.findById(preciousId).get();
        candidate.setVoteCount(10);
        candidateRepository.save(candidate);

        candidate = candidateRepository.findById(johnId).get();
        candidate.setVoteCount(4);
        candidateRepository.save(candidate);

        restTestClient.get()
                .uri(url("/results"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.President[0].firstName").isEqualTo("Precious")
                .jsonPath("$.data.President[1].firstName").isEqualTo("John");
    }

    // ─── GET /results/{position} ──────────────────────────────────────────────

    @Test
    @DisplayName("Test that get results by position returns only that position")
    void getResultsByPosition_successTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        CandidateRequest candidateJane = new CandidateRequest();
        candidateJane.setFirstName("Jane");
        candidateJane.setLastName("Doe");
        candidateJane.setPosition("Governor");
        candidateService.createCandidate(candidateJane);

        restTestClient.get()
                .uri(url("/results/president"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].position").isEqualTo("President")
                .jsonPath("$.data[1].position").isEqualTo("President");
    }

    @Test
    @DisplayName("Test that get results by position orders by vote count")
    void getResultsByPosition_orderedByVoteCount_Test() {
        String preciousId = candidateService.createCandidate(candidatePrecious).getId();
        String johnId = candidateService.createCandidate(candidateJohn).getId();

        Candidate candidate = candidateRepository.findById(preciousId).get();
        candidate.setVoteCount(2);
        candidateRepository.save(candidate);

        candidate = candidateRepository.findById(johnId).get();
        candidate.setVoteCount(4);
        candidateRepository.save(candidate);

        restTestClient.get()
                .uri(url("/results/president"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[1].firstName").isEqualTo("Precious");
    }

    @Test
    @DisplayName("Test that get results by unknown position returns empty list")
    void getResultsByPosition_unknownPosition_returnsEmpty_Test() {
        candidateService.createCandidate(candidatePrecious);

        restTestClient.get()
                .uri(url("/results/senator"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(0);
    }

}