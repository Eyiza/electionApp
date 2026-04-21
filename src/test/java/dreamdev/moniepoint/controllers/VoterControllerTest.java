package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
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

import java.time.LocalDateTime;

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
    private ElectionRepository electionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private TestHelper testHelper;

    private Election ongoingElection;
    private Position presidentPosition;
    private Position governorPosition;
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
        positionRepository.deleteAll();
        electionRepository.deleteAll();

        voterJamie = new VoterRequest();
        voterJamie.setName("Jamie");
        voterJamie.setNin("4567");

        voterCarrie = new VoterRequest();
        voterCarrie.setName("Carrie");
        voterCarrie.setNin("6789");

        // register candidates while election is upcoming
        Election upcomingElection = testHelper.createUpcomingElection();
        presidentPosition = testHelper.createPosition("President", upcomingElection.getId());
        governorPosition = testHelper.createPosition("Governor", upcomingElection.getId());

        candidatePrecious = testHelper.buildCandidateRequest("Precious", "Michael", presidentPosition.getId());
        candidateJohn = testHelper.buildCandidateRequest("John", "Doe", governorPosition.getId());

        // push start time back so election is now ongoing
        upcomingElection.setStartDateTime(upcomingElection.getStartDateTime().minusHours(2));
        electionRepository.save(upcomingElection);

        ongoingElection = electionRepository.findById(upcomingElection.getId()).get();
    }

    private void makeElectionUpcoming() {
        ongoingElection.setStartDateTime(LocalDateTime.now().plusHours(2));
        ongoingElection.setEndDateTime(LocalDateTime.now().plusHours(5));
        electionRepository.save(ongoingElection);
    }

    private void makeElectionOngoing() {
        ongoingElection.setStartDateTime(LocalDateTime.now().minusHours(1));
        ongoingElection.setEndDateTime(LocalDateTime.now().plusHours(2));
        electionRepository.save(ongoingElection);
    }

    @Test
    @DisplayName("Test successful voter registration")
    void registerVoter_successTest() {
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated()
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
                .expectStatus().isCreated();

        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all voters returns empty list")
    void getVoters_isEmptyTest() {
        restTestClient.get()
                .uri(url("/voters"))
                .exchange()
                .expectStatus().isOk()
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
                .expectStatus().isCreated();

        restTestClient.post()
                .uri(url("/voter"))
                .body(voterCarrie)
                .exchange()
                .expectStatus().isCreated();

        restTestClient.get()
                .uri(url("/voters"))
                .exchange()
                .expectStatus().isOk()
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
                .expectStatus().isCreated()
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
                .expectStatus().isOk()
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
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote succeeds and returns vote response")
    void vote_successTest() {
        makeElectionUpcoming();
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();
        makeElectionOngoing();
        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", candidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("Jamie")
                .jsonPath("$.data.candidateName").isEqualTo("Precious Michael")
                .jsonPath("$.data.votedPositions").isArray();
    }

    @Test
    @DisplayName("Test voter can vote for two different positions")
    void vote_twoPositions_successTest() {
        makeElectionUpcoming();
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated();

        String presidentialCandidateId = candidateService.createCandidate(candidatePrecious).getId();
        String governorCandidateId = candidateService.createCandidate(candidateJohn).getId();

        makeElectionOngoing();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", presidentialCandidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isOk();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", governorCandidateId, governorPosition.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.votedPositions.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test voter cannot vote twice for same position")
    void vote_alreadyVotedForPositionFailsTest() {
        makeElectionUpcoming();
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();
        makeElectionOngoing();
        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", candidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isOk();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", candidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote with unregistered NIN fails")
    void vote_unregisteredVoterFailsTest() {
        makeElectionUpcoming();
        String candidateId = candidateService.createCandidate(candidatePrecious).getId();
        makeElectionOngoing();
        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("0000", candidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isBadRequest()
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
                .expectStatus().isCreated();

        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", "nonexistentid123", presidentPosition.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote with mismatched positionId fails")
    void vote_positionMismatchFailsTest() {
        makeElectionUpcoming();
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated();

        // precious is running for president, but we pass governorPosition
        String candidateId = candidateService.createCandidate(candidatePrecious).getId();
        makeElectionOngoing();
        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", candidateId, governorPosition.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test vote fails when election has ended")
    void vote_electionEndedFailsTest() {
        makeElectionUpcoming();
        restTestClient.post()
                .uri(url("/voter"))
                .body(voterJamie)
                .exchange()
                .expectStatus().isCreated();

        String candidateId = candidateService.createCandidate(candidatePrecious).getId();

        // push end time to the past
        ongoingElection.setEndDateTime(ongoingElection.getEndDateTime().minusHours(10));
        electionRepository.save(ongoingElection);

        restTestClient.patch()
                .uri(url("/vote"))
                .body(testHelper.buildVoteRequest("4567", candidateId, presidentPosition.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }
}