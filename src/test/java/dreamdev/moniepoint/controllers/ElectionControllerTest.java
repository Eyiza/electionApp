package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.utils.ElectionStatus;
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
class ElectionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private TestHelper testHelper;

    private String url(String path) {
        return "http://localhost:%d%s".formatted(port, path);
    }

    @BeforeEach
    void setUp() {
        electionRepository.deleteAll();
    }

    private ElectionRequest buildRequest(String title, int startOffsetHours, int endOffsetHours) {
        ElectionRequest request = new ElectionRequest();
        request.setTitle(title);
        request.setStartDateTime(ElectionStatus.now().plusHours(startOffsetHours));
        request.setEndDateTime(ElectionStatus.now().plusHours(endOffsetHours));
        return request;
    }

    @Test
    @DisplayName("Test successful election creation")
    void createElection_successTest() {
        restTestClient.post()
                .uri(url("/election"))
                .body(buildRequest("2027 Election", 1, 5))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.title").isEqualTo("2027 Election")
                .jsonPath("$.data.status").isEqualTo("UPCOMING")
                .jsonPath("$.data.id").exists();
    }

    @Test
    @DisplayName("Test election creation fails when end date is before start date")
    void createElection_endBeforeStart_failsTest() {
        restTestClient.post()
                .uri(url("/election"))
                .body(buildRequest("Bad Election", 5, 1))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test ongoing election has correct status")
    void createElection_ongoingStatusTest() {
        restTestClient.post()
                .uri(url("/election"))
                .body(buildRequest("Ongoing Election", -1, 5))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.status").isEqualTo("ONGOING");
    }

    @Test
    @DisplayName("Test ended election has correct status")
    void createElection_endedStatusTest() {
        restTestClient.post()
                .uri(url("/election"))
                .body(buildRequest("Past Election", -5, -1))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.status").isEqualTo("ENDED");
    }


    @Test
    @DisplayName("Test get election by id succeeds")
    void getElection_successTest() {
        String responseBody = restTestClient.post()
                .uri(url("/election"))
                .body(buildRequest("2027 Election", 1, 5))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String id = new tools.jackson.databind.ObjectMapper()
                .readTree(responseBody).path("data").path("id").asText();

        restTestClient.get()
                .uri(url("/election/" + id))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.title").isEqualTo("2027 Election")
                .jsonPath("$.data.status").isEqualTo("UPCOMING");
    }

    @Test
    @DisplayName("Test get election by invalid id fails")
    void getElection_invalidIdFailsTest() {
        restTestClient.get()
                .uri(url("/election/nonexistentid123"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    @DisplayName("Test get all elections returns empty list")
    void getAllElections_emptyTest() {
        restTestClient.get()
                .uri(url("/elections"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    @DisplayName("Test get all elections returns list of 2")
    void getAllElections_listIs2Test() {
        testHelper.createUpcomingElection();
        testHelper.createOngoingElection();

        restTestClient.get()
                .uri(url("/elections"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.length()").isEqualTo(2);
    }
}