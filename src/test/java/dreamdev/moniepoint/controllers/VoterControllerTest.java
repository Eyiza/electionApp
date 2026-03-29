package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class VoterControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CandidateService candidateService;

    private VoterRequest voterJamie;
    private VoterRequest voterCarrie;
    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        voterRepository.deleteAll();
        candidateRepository.deleteAll();

        voterJamie = new VoterRequest();
        voterJamie.setName("Jamie");
        voterJamie.setNin("4567");

        voterCarrie = new VoterRequest();
        voterCarrie.setName("Carrie");
        voterCarrie.setNin("6789");

        candidatePrecious = new CandidateRequest();
        candidatePrecious.setFirstName("Precious");
        candidatePrecious.setLastName("Michael");
        candidatePrecious.setPosition("President");

        candidateJohn = new CandidateRequest();
        candidateJohn.setFirstName("John");
        candidateJohn.setLastName("Doe");
        candidateJohn.setPosition("Governor");
    }

    private VoteRequest buildVoteRequest(String nin, String candidateId, String position) {
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setNin(nin);
        voteRequest.setCandidateId(candidateId);
        voteRequest.setCandidatePosition(position);
        return voteRequest;
    }

    @Test
    @DisplayName("Test successful voter registration")
    void registerVoter_successTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Jamie")
                .jsonPath("$.data.nin").isEqualTo("4567")
                .jsonPath("$.data.id").exists()
                .jsonPath("$.data.votedPositions").isEmpty();
    }

    @Test
    @DisplayName("Test duplicate voter registration fails")
    void registerVoter_duplicateFailsTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all voters returns empty list")
    void getVoters_isEmptyTest() {
        restTestClient.get()
                .uri(url("/voters"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Test get all voters returns list of 2")
    void getVoters_listIs2Test() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        restTestClient.post()
                .uri(url("/voter"))
                .body(voterCarrie)
                .exchange()
                .expectStatus()
                .isCreated();

        restTestClient.get()
                .uri(url("/voters"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test get voter by id succeeds")
    void getVoterById_successTest() {
        String responseBody = restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String id = objectMapper.readTree(responseBody)
                .path("data")
                .path("id")
                .asText();

        restTestClient.get()
                .uri(url("/voter/" + id))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Jamie")
                .jsonPath("$.data.nin").isEqualTo("4567");
    }

    @Test
    @DisplayName("Test get voter by invalid id fails")
    void getVoterById_invalidIdFailsTest() {
        restTestClient.get()
                .uri(url("/voter/nonexistentid123"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote succeeds and returns vote response")
    void vote_successTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", candidateId, "President"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Jamie")
                .jsonPath("$.data.candidateName").isEqualTo("Precious Michael")
                .jsonPath("$.data.votedPositions").isArray();
    }

    @Test
    @DisplayName("Test voter can vote for two different positions")
    void vote_twoPositions_successTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        String presidentialCandidateId = candidateService.createCandidate(candidatePrecious).getId();
        String governorCandidateId = candidateService.createCandidate(candidateJohn).getId();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", presidentialCandidateId, "President"))
                .exchange()
                .expectStatus()
                .isOk();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", governorCandidateId, "Governor"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.votedPositions.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test voter cannot vote twice for same position")
    void vote_alreadyVotedForPositionFailsTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", candidateId, "President"))
                .exchange()
                .expectStatus()
                .isOk();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", candidateId, "President"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote with unregistered NIN fails")
    void vote_unregisteredVoterFailsTest() {
        String candidateId = candidateService.createCandidate(candidatePrecious).getId();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("0000", candidateId, "President"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote with invalid candidate id fails")
    void vote_invalidCandidateIdFailsTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", "invalid", "President"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote with mismatched position fails")
    void vote_positionMismatchFailsTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus()
                .isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(buildVoteRequest("4567", candidateId, "Governor"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }
}