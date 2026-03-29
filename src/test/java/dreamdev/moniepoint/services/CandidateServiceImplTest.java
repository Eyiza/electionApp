package dreamdev.moniepoint.services;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import dreamdev.moniepoint.exceptions.InvalidVoterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
}

