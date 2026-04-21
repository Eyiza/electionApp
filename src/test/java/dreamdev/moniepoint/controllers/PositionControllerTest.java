package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.PositionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PositionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private TestHelper testHelper;

    private Election upcomingElection;

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        positionRepository.deleteAll();
        electionRepository.deleteAll();
        upcomingElection = testHelper.createUpcomingElection();
    }

    private PositionRequest buildRequest(String title, String electionId) {
        PositionRequest request = new PositionRequest();
        request.setTitle(title);
        request.setElectionId(electionId);
        return request;
    }


    @Test
    @DisplayName("Test successful position creation")
    void createPosition_successTest() {
        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", upcomingElection.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.title").isEqualTo("President")
                .jsonPath("$.data.electionId").isEqualTo(upcomingElection.getId())
                .jsonPath("$.data.id").exists();
    }

    @Test
    @DisplayName("Test position creation fails with invalid election id")
    void createPosition_invalidElectionIdFailsTest() {
        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", "invalidId"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test position creation fails when election is ongoing")
    void createPosition_ongoingElectionFailsTest() {
        Election ongoingElection = testHelper.createOngoingElection();

        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", ongoingElection.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test position creation fails when election has ended")
    void createPosition_endedElectionFailsTest() {
        Election endedElection = testHelper.createEndedElection();

        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", endedElection.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test duplicate position creation fails")
    void createPosition_duplicateFailsTest() {
        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", upcomingElection.getId()))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", upcomingElection.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test duplicate position check is case insensitive")
    void createPosition_caseInsensitiveDuplicateFailsTest() {
        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", upcomingElection.getId()))
                .exchange()
                .expectStatus().isCreated();

        restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("president", upcomingElection.getId()))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }


    @Test
    @DisplayName("Test get position by id succeeds")
    void getPosition_successTest() {
        String responseBody = restTestClient.post()
                .uri(url("/position"))
                .body(buildRequest("President", upcomingElection.getId()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String id = new tools.jackson.databind.ObjectMapper()
                .readTree(responseBody).path("data").path("id").asText();

        restTestClient.get()
                .uri(url("/position/" + id))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.title").isEqualTo("President")
                .jsonPath("$.data.electionId").isEqualTo(upcomingElection.getId());
    }

    @Test
    @DisplayName("Test get position by invalid id fails")
    void getPosition_invalidIdFailsTest() {
        restTestClient.get()
                .uri(url("/position/nonexistentid123"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }


    @Test
    @DisplayName("Test get positions by election returns correct list")
    void getPositionsByElection_successTest() {
        testHelper.createPosition("President", upcomingElection.getId());
        testHelper.createPosition("Governor", upcomingElection.getId());

        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/positions"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    @DisplayName("Test get positions by invalid election id fails")
    void getPositionsByElection_invalidElectionIdFailsTest() {
        restTestClient.get()
                .uri(url("/election/invalidId/positions"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get positions by election returns empty list when no positions")
    void getPositionsByElection_emptyListTest() {
        restTestClient.get()
                .uri(url("/election/" + upcomingElection.getId() + "/positions"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(0);
    }
}