package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
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
    private ElectionRepository electionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private TestHelper testHelper;

    private Election upcomingElection;
    private Position presidentPosition;
    private Position governorPosition;
    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
        positionRepository.deleteAll();
        electionRepository.deleteAll();

        upcomingElection = testHelper.createUpcomingElection();
        presidentPosition = testHelper.createPosition("President", upcomingElection.getId());
        governorPosition = testHelper.createPosition("Governor", upcomingElection.getId());

        candidatePrecious = testHelper.buildCandidateRequest("Precious", "Michael", presidentPosition.getId());
        candidateJohn = testHelper.buildCandidateRequest("John", "Doe", presidentPosition.getId());
    }

    @Test
    @DisplayName("Test successful candidate creation")
    void addCandidateSuccess() {
        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.positionTitle").isEqualTo("President")
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
                .expectStatus().isCreated();

        restTestClient.post()
                .uri(url("/candidate"))
                .body(candidateJohn)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test candidate registration fails when election is ongoing")
    void addCandidate_whenElectionOngoing_failsTest() {
        Election ongoingElection = testHelper.createOngoingElection();
        Position position = testHelper.createPosition("Senator", ongoingElection.getId());
        CandidateRequest request = testHelper.buildCandidateRequest("Jane", "Smith", position.getId());

        restTestClient.post()
                .uri(url("/candidate"))
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test candidate registration fails when election has ended")
    void addCandidate_whenElectionEnded_failsTest() {
        Election endedElection = testHelper.createEndedElection();
        Position position = testHelper.createPosition("Senator", endedElection.getId());
        CandidateRequest request = testHelper.buildCandidateRequest("Jane", "Smith", position.getId());

        restTestClient.post()
                .uri(url("/candidate"))
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all candidates returns empty list")
    void getAllCandidates_isEmptyTest() {
        ApiResponse response = new ApiResponse(true, new ArrayList<>());

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json(objectMapper.writeValueAsString(response));
    }

    @Test
    @DisplayName("Test get all candidates returns list of 2")
    void getAllCandidates_listIs2Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test get candidate by id succeeds")
    void getCandidateById_successTest() {
        String id = candidateService.createCandidate(candidateJohn).getId();
        restTestClient.get()
                .uri(url("/candidate/" + id))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.firstName").isEqualTo("John")
                .jsonPath("$.data.lastName").isEqualTo("Doe")
                .jsonPath("$.data.positionTitle").isEqualTo("President")
                .jsonPath("$.data.voteCount").isEqualTo(0);
    }

    @Test
    @DisplayName("Test get candidate by invalid id fails")
    void getCandidateById_invalidIdFailsTest() {
        restTestClient.get()
                .uri(url("/candidate/123"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test search candidates by name succeeds")
    void getCandidateByName_isSuccessTest() {
        candidateService.createCandidate(candidateJohn);

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates/search?firstName=John&lastName=Doe"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[0].lastName").isEqualTo("Doe")
                .jsonPath("$.data[0].positionTitle").isEqualTo("President")
                .jsonPath("$.data[0].voteCount").isEqualTo(0);
    }

    @Test
    @DisplayName("Test search candidates with partial fields returns similar matches")
    void searchCandidates_byIncompleteFieldsTest() {
        candidateService.createCandidate(candidateJohn);

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates/search?firstName=jo&lastName=d"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[0].lastName").isEqualTo("Doe");
    }

    @Test
    @DisplayName("Test search candidates case insensitive")
    void searchCandidates_caseInsensitiveTest() {
        candidateService.createCandidate(candidatePrecious);

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates/search?firstName=PRECIOUS&lastName=MICHAEL"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(1)
                .jsonPath("$.data[0].firstName").isEqualTo("Precious");
    }

    @Test
    @DisplayName("Test search with no params returns all candidates")
    void searchCandidates_noParamsReturnsAllTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/candidates/search"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test get results returns empty when no candidates")
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
    @DisplayName("Test get results groups candidates by position")
    void getResults_groupedByPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        CandidateRequest candidateJane = testHelper.buildCandidateRequest("Jane", "Doe", governorPosition.getId());
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
    @DisplayName("Test get results orders candidates by descending vote count")
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

    @Test
    @DisplayName("Test get results by positionId returns only that position")
    void getResultsByPosition_successTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        CandidateRequest candidateJane = testHelper.buildCandidateRequest("Jane", "Doe", governorPosition.getId());
        candidateService.createCandidate(candidateJane);

        restTestClient.get()
                .uri(url("/results/" + presidentPosition.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].positionTitle").isEqualTo("President")
                .jsonPath("$.data[1].positionTitle").isEqualTo("President");
    }

    @Test
    @DisplayName("Test get results by positionId orders by vote count")
    void getResultsByPosition_orderedByVoteCount_Test() {
        String preciousId = candidateService.createCandidate(candidatePrecious).getId();
        String johnId = candidateService.createCandidate(candidateJohn).getId();

        Candidate candidate = candidateRepository.findById(preciousId).get();
        candidate.setVoteCount(2);
        candidateRepository.save(candidate);

        candidate = candidateRepository.findById(johnId).get();
        candidate.setVoteCount(8);
        candidateRepository.save(candidate);

        restTestClient.get()
                .uri(url("/results/" + presidentPosition.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].firstName").isEqualTo("John")
                .jsonPath("$.data[1].firstName").isEqualTo("Precious");
    }

    @Test
    @DisplayName("Test get results by unknown positionId returns empty list")
    void getResultsByPosition_unknownPosition_returnsEmpty_Test() {
        candidateService.createCandidate(candidatePrecious);

        restTestClient.get()
                .uri(url("/results/unknownPositionId"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(0);
    }
}