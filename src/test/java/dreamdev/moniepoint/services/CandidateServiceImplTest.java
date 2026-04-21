package dreamdev.moniepoint.services;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.ElectionNotActiveException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CandidateServiceImplTest {

    @Autowired
    private CandidateService candidateService;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ElectionRepository electionRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private TestHelper testHelper;

    private Election upcomingElection;
    private Position presidentPosition;
    private Position governorPosition;
    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

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
    public void candidateRepoIsEmpty_Test() {
        assertEquals(0L, candidateRepository.count());
    }

    @Test
    public void createCandidate_countIsOneTest() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate(candidatePrecious);
        assertEquals(1L, candidateRepository.count());
    }

    @Test
    public void createTwiceWithSameFirstAndLastName_throwExceptionTest() {
        candidateService.createCandidate(candidatePrecious);
        assertThrows(DuplicateCandidateException.class, () -> candidateService.createCandidate(candidatePrecious));
        assertEquals(1L, candidateRepository.count());
    }

    @Test
    public void createCandidate_whenElectionIsOngoing_throwsExceptionTest() {
        Election ongoingElection = testHelper.createOngoingElection();
        Position position = testHelper.createPosition("Senator", ongoingElection.getId());
        CandidateRequest request = testHelper.buildCandidateRequest("Jane", "Smith", position.getId());

        assertThrows(ElectionNotActiveException.class, () -> candidateService.createCandidate(request));
    }

    @Test
    public void createCandidate_whenElectionHasEnded_throwsExceptionTest() {
        Election endedElection = testHelper.createEndedElection();
        Position position = testHelper.createPosition("Senator", endedElection.getId());
        CandidateRequest request = testHelper.buildCandidateRequest("Jane", "Smith", position.getId());

        assertThrows(ElectionNotActiveException.class, () -> candidateService.createCandidate(request));
    }

    @Test
    public void addTwoCandidate_countIsTwoTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        assertEquals(2L, candidateRepository.count());
    }

    @Test
    public void getAllCandidates_emptyListTest() {
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void getAllCandidates_returnsAllCandidatesTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        assertEquals(2, candidates.size());
    }

    @Test
    public void getAllCandidates_containsCorrectDataTest() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        List<CandidateResponse> candidates = candidateService.getAllCandidates();
        assertEquals(2, candidates.size());
        assertEquals("Precious", candidates.get(0).getFirstName());
        assertEquals("Michael", candidates.get(0).getLastName());
        assertEquals("President", candidates.get(0).getPositionTitle());

        assertEquals("John", candidates.get(1).getFirstName());
        assertEquals("Doe", candidates.get(1).getLastName());
        assertEquals("President", candidates.get(1).getPositionTitle());
    }

    @Test
    public void getCandidateById_Test() {
        CandidateResponse created = candidateService.createCandidate(candidateJohn);
        CandidateResponse found = candidateService.getCandidate(created.getId());
        assertEquals("John", found.getFirstName());
        assertEquals("Doe", found.getLastName());
        assertEquals("President", found.getPositionTitle());
        assertEquals(0, found.getVoteCount());
    }

    @Test
    public void getCandidateByInvalidId_throwExceptionTest() {
        assertThrows(InvalidCandidateIdException.class, () -> candidateService.getCandidate("Invalid"));
    }

    @Test
    public void searchCandidateByFirstNameAndLastName_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        List<CandidateResponse> candidates = candidateService.searchCandidates("precious", "michael", presidentPosition.getId());
        assertEquals(1, candidates.size());
        assertEquals("Precious", candidates.get(0).getFirstName());
        assertEquals("Michael", candidates.get(0).getLastName());
        assertEquals("President", candidates.get(0).getPositionTitle());
    }

    @Test
    public void getResults_isEmptyWhenNoCandidateExist_Test() {
        Map<String, List<CandidateResponse>> results = candidateService.getResults();
        assertTrue(results.isEmpty());
    }

    @Test
    public void getResults_isGroupedByPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        CandidateRequest candidateJane = testHelper.buildCandidateRequest("Jane", "Doe", governorPosition.getId());
        candidateService.createCandidate(candidateJane);

        Map<String, List<CandidateResponse>> results = candidateService.getResults();
        assertEquals(2, results.size());
        assertTrue(results.containsKey("President"));
        assertTrue(results.containsKey("Governor"));
        assertEquals(2, results.get("President").size());
        assertEquals(1, results.get("Governor").size());
    }

    @Test
    public void getResults_candidatesAreOrderedByVoteCount_Test() {
        String preciousId = candidateService.createCandidate(candidatePrecious).getId();
        String johnId = candidateService.createCandidate(candidateJohn).getId();

        Candidate candidate = candidateRepository.findById(preciousId).get();
        candidate.setVoteCount(10);
        candidateRepository.save(candidate);

        candidate = candidateRepository.findById(johnId).get();
        candidate.setVoteCount(4);
        candidateRepository.save(candidate);

        Map<String, List<CandidateResponse>> results = candidateService.getResults();
        List<CandidateResponse> presidentialResults = results.get("President");
        assertEquals("Precious", presidentialResults.get(0).getFirstName());
        assertEquals("John", presidentialResults.get(1).getFirstName());
    }

    @Test
    public void getResultsByPosition_shouldReturnOnlyThatPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        CandidateRequest candidateJane = testHelper.buildCandidateRequest("Jane", "Doe", governorPosition.getId());
        candidateService.createCandidate(candidateJane);

        List<CandidateResponse> presidentialResults = candidateService.getResultsByPosition(presidentPosition.getId());
        assertEquals(2, presidentialResults.size());
        for (CandidateResponse c : presidentialResults) {
            assertEquals("President", c.getPositionTitle());
        }
    }

    @Test
    public void getResultsByPosition_candidatesAreOrderedByVoteCount_Test() {
        String preciousId = candidateService.createCandidate(candidatePrecious).getId();
        String johnId = candidateService.createCandidate(candidateJohn).getId();

        Candidate candidate = candidateRepository.findById(preciousId).get();
        candidate.setVoteCount(2);
        candidateRepository.save(candidate);

        candidate = candidateRepository.findById(johnId).get();
        candidate.setVoteCount(5);
        candidateRepository.save(candidate);

        List<CandidateResponse> results = candidateService.getResultsByPosition(presidentPosition.getId());
        assertEquals("John", results.get(0).getFirstName());
        assertEquals("Precious", results.get(1).getFirstName());
    }

    @Test
    public void getResultsByPosition_isEmptyForUnknownPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        List<CandidateResponse> results = candidateService.getResultsByPosition("unknownPositionId");
        assertTrue(results.isEmpty());
    }
}