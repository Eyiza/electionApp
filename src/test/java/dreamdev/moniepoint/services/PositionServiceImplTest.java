package dreamdev.moniepoint.services;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.responses.PositionResponse;
import dreamdev.moniepoint.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PositionServiceImplTest {

    @Autowired
    private PositionService positionService;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private ElectionRepository electionRepository;
    @Autowired
    private TestHelper testHelper;

    private Election upcomingElection;

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
    public void createPosition_countIsOneTest() {
        positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        assertEquals(1L, positionRepository.count());
    }

    @Test
    public void createPosition_withInvalidElectionId_throwsExceptionTest() {
        assertThrows(InvalidElectionException.class, () ->
                positionService.createPosition(buildRequest("President", "invalidId")));
    }

    @Test
    public void createPosition_whenElectionIsOngoing_throwsExceptionTest() {
        Election ongoingElection = testHelper.createOngoingElection();
        assertThrows(ElectionNotActiveException.class, () ->
                positionService.createPosition(buildRequest("President", ongoingElection.getId())));
    }

    @Test
    public void createPosition_whenElectionHasEnded_throwsExceptionTest() {
        Election endedElection = testHelper.createEndedElection();
        assertThrows(ElectionNotActiveException.class, () ->
                positionService.createPosition(buildRequest("President", endedElection.getId())));
    }

    @Test
    public void createDuplicatePosition_throwsExceptionTest() {
        positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        assertThrows(DuplicatePositionException.class, () ->
                positionService.createPosition(buildRequest("President", upcomingElection.getId())));
    }

    @Test
    public void createPosition_caseInsensitiveDuplicateCheck_throwsExceptionTest() {
        positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        assertThrows(DuplicatePositionException.class, () ->
                positionService.createPosition(buildRequest("president", upcomingElection.getId())));
    }

    @Test
    public void getPosition_byId_successTest() {
        PositionResponse created = positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        PositionResponse found = positionService.getPosition(created.getId());
        assertEquals("President", found.getTitle());
        assertEquals(upcomingElection.getId(), found.getElectionId());
    }

    @Test
    public void getPosition_byInvalidId_throwsExceptionTest() {
        assertThrows(InvalidPositionException.class, () -> positionService.getPosition("invalidId"));
    }

    @Test
    public void getPositionsByElection_returnsCorrectListTest() {
        positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        positionService.createPosition(buildRequest("Governor", upcomingElection.getId()));

        List<PositionResponse> positions = positionService.getPositionsByElection(upcomingElection.getId());
        assertEquals(2, positions.size());
    }

    @Test
    public void getPositionsByElection_withInvalidElectionId_throwsExceptionTest() {
        assertThrows(InvalidElectionException.class, () ->
                positionService.getPositionsByElection("invalidId"));
    }

    @Test
    public void createPosition_addsPositionIdToElection_Test() {
        PositionResponse position = positionService.createPosition(buildRequest("President", upcomingElection.getId()));
        Election updated = electionRepository.findById(upcomingElection.getId()).get();
        assertTrue(updated.getPositionIds().contains(position.getId()));
    }
}