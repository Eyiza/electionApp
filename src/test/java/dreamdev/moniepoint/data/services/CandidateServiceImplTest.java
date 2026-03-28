package dreamdev.moniepoint.data.services;
import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import dreamdev.moniepoint.services.CandidateService;
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

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
    }

    @Test
    public void candidateRepoIsEmpty_Test() {
        assertEquals(0L, candidateRepository.count());
    }

    @Test
    public void createCandidate_countIsOneTest() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate("Precious", "Michael", "President");
        assertEquals(1L, candidateRepository.count());
    }

    @Test
    public void createTwiceWithSameFirstAndLastName_throwExceptionTest() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate("Precious", "Michael", "President");
        assertThrows(DuplicateCandidateException.class, ()-> candidateService.createCandidate("Precious", "Michael", "President"));
        assertEquals(1L, candidateRepository.count());
    }

    @Test
    public void AddTwoCandidate_countIsTwoTest() {
        candidateService.createCandidate("Precious", "Michael", "President");
        candidateService.createCandidate("John", "Doe", "President");
        assertEquals(2L, candidateRepository.count());
    }

    @Test
    public void getAllCandidates_emptyListTest() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        assertTrue(candidates.isEmpty());
    }

    @Test
    public void getAllCandidates_returnsAllCandidatesTest() {
        candidateService.createCandidate("Precious", "Michael", "President");
        candidateService.createCandidate("John", "Doe", "President");
        List<Candidate> candidates = candidateService.getAllCandidates();

        assertEquals(2, candidates.size());
    }

    @Test
    public void getAllCandidates_containsCorrectDataTest() {
        candidateService.createCandidate("Precious", "Michael", "President");
        candidateService.createCandidate("John", "Doe", "President");

        List<Candidate> candidates = candidateService.getAllCandidates();
        assertEquals(2, candidates.size());
        assertEquals("Precious", candidates.get(0).getFirstName());
        assertEquals("Michael", candidates.get(0).getLastName());
        assertEquals("President", candidates.get(0).getPosition());

        assertEquals("John", candidates.get(1).getFirstName());
        assertEquals("Doe", candidates.get(1).getLastName());
        assertEquals("President", candidates.get(1).getPosition());
    }

    @Test
    public void findCandidateByFirstNameAndLastName_Test() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate("Precious", "Michael", "President");
        candidateService.createCandidate("John", "Doe", "President");
        candidateService.createCandidate("Joe", "Barnes", "President");
        Candidate candidate = candidateService.getCandidate("John", "Doe");
        assertEquals("John", candidate.getFirstName());
        assertEquals("Doe", candidate.getLastName());
        assertEquals("President", candidate.getPosition());
        assertEquals(0, candidate.getVoteCount());
        assertEquals(3L, candidateRepository.count());
    }

    @Test
    public void findCandidateByFirstNameAndLastName_ThrowExceptionTest() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate("Precious", "Michael", "President");
        candidateService.createCandidate("John", "Doe", "President");
        assertEquals(2L, candidateRepository.count());
        assertThrows(InvalidCandidateIdException.class, ()-> candidateService.getCandidate("John", "Michael"));
    }

    @Test
    public void voteCandidate_voteCountIsOneTest() {
        candidateService.createCandidate("Precious", "Michael", "President");
        assertEquals(1L, candidateRepository.count());
        candidateService.voteCandidate("Precious", "Michael", "President");
        Candidate candidate = candidateService.getCandidate("Precious", "Michael");
        assertEquals(1, candidate.getVoteCount());
    }
}

