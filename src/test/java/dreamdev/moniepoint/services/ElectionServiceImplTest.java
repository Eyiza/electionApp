package dreamdev.moniepoint.services;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.responses.ElectionResponse;
import dreamdev.moniepoint.exceptions.InvalidElectionException;
import dreamdev.moniepoint.utils.ElectionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ElectionServiceImplTest {

    @Autowired
    private ElectionService electionService;
    @Autowired
    private ElectionRepository electionRepository;
    @Autowired
    private TestHelper testHelper;

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
    public void createElection_countIsOneTest() {
        electionService.createElection(buildRequest("2027 Election", 1, 5));
        assertEquals(1L, electionRepository.count());
    }

    @Test
    public void createElection_endDateBeforeStartDate_throwsExceptionTest() {
        ElectionRequest request = buildRequest("Bad Election", 5, 1);
        assertThrows(InvalidElectionException.class, () -> electionService.createElection(request));
    }

    @Test
    public void createElection_statusIsUpcomingTest() {
        ElectionResponse response = electionService.createElection(buildRequest("2027 Election", 1, 5));
        assertEquals("UPCOMING", response.getStatus());
    }

    @Test
    public void createElection_statusIsOngoingTest() {
        ElectionResponse response = electionService.createElection(buildRequest("2027 Election", -1, 5));
        assertEquals("ONGOING", response.getStatus());
    }

    @Test
    public void createElection_statusIsEndedTest() {
        ElectionResponse response = electionService.createElection(buildRequest("2027 Election", -5, -1));
        assertEquals("ENDED", response.getStatus());
    }

    @Test
    public void getElection_byId_successTest() {
        ElectionResponse created = electionService.createElection(buildRequest("2027 Election", 1, 5));
        ElectionResponse found = electionService.getElection(created.getId());
        assertEquals("2027 Election", found.getTitle());
        assertEquals("UPCOMING", found.getStatus());
    }

    @Test
    public void getElection_byInvalidId_throwsExceptionTest() {
        assertThrows(InvalidElectionException.class, () -> electionService.getElection("invalidId"));
    }

    @Test
    public void getAllElections_emptyListTest() {
        List<ElectionResponse> elections = electionService.getAllElections();
        assertTrue(elections.isEmpty());
    }

    @Test
    public void getAllElections_returnsAllTest() {
        electionService.createElection(buildRequest("Election A", 1, 5));
        electionService.createElection(buildRequest("Election B", 2, 6));
        assertEquals(2, electionService.getAllElections().size());
    }
}