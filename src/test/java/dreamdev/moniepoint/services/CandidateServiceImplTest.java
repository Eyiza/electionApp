package dreamdev.moniepoint.services;
import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
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

    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

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
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate(candidatePrecious);
        assertThrows(DuplicateCandidateException.class, ()-> candidateService.createCandidate(candidatePrecious));
        assertEquals(1L, candidateRepository.count());
    }

    @Test
    public void AddTwoCandidate_countIsTwoTest() {
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
        assertEquals("President", candidates.get(0).getPosition());

        assertEquals("John", candidates.get(1).getFirstName());
        assertEquals("Doe", candidates.get(1).getLastName());
        assertEquals("President", candidates.get(1).getPosition());
    }

    @Test
    public void getCandidateById_Test() {
        CandidateResponse candidate = candidateService.createCandidate(candidateJohn);
        assertEquals("John", candidate.getFirstName());
        assertEquals("Doe", candidate.getLastName());
        assertEquals("President", candidate.getPosition());
        assertEquals(0, candidate.getVoteCount());
    }

    @Test
    public void getCandidateByInvalidId_throwExceptionTest() {
        CandidateResponse candidate = candidateService.createCandidate(candidateJohn);
        assertThrows(InvalidCandidateIdException.class, ()-> candidateService.getCandidate("Invalid"));
    }

    @Test
    public void searchCandidateByFirstNameAndLastName_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);
        List<CandidateResponse> candidates = candidateService.searchCandidates("precious", "michael", "pres");
        assertEquals(1, candidates.size());
        assertEquals("Precious", candidates.get(0).getFirstName());
        assertEquals("Michael", candidates.get(0).getLastName());
        assertEquals("President", candidates.get(0).getPosition());
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

        CandidateRequest candidateJane = new CandidateRequest();
        candidateJane.setFirstName("Jane");
        candidateJane.setLastName("Doe");
        candidateJane.setPosition("Governor");
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
        assertEquals(1, results.size());
        List<CandidateResponse> presidentialResults = results.get("President");

        assertEquals("Precious", presidentialResults.get(0).getFirstName());
        assertEquals("John", presidentialResults.get(1).getFirstName());
    }

    @Test
    public void getResultsByPosition_shouldReturnsOnlyThatPosition_Test() {
        candidateService.createCandidate(candidatePrecious);
        candidateService.createCandidate(candidateJohn);

        CandidateRequest candidateJane = new CandidateRequest();
        candidateJane.setFirstName("Jane");
        candidateJane.setLastName("Doe");
        candidateJane.setPosition("Governor");
        candidateService.createCandidate(candidateJane);

        List<CandidateResponse> presidentialResults = candidateService.getResultsByPosition("president");

        assertEquals(2, presidentialResults.size());
        for (CandidateResponse candidate : presidentialResults) {
            assertEquals("President", candidate.getPosition());
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

        List<CandidateResponse> results = candidateService.getResultsByPosition("President");

        assertEquals("John", results.get(0).getFirstName());
        assertEquals("Precious", results.get(1).getFirstName());
    }

    @Test
    public void getResultsByPosition_isEmptyForUnknownPosition_Test() {
        candidateService.createCandidate(candidatePrecious);

        List<CandidateResponse> results = candidateService.getResultsByPosition("Senator");
        assertTrue(results.isEmpty());
    }

}

