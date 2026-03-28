package dreamdev.moniepoint.data.services;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.services.CandidateService;
import dreamdev.moniepoint.services.CandidateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    public void createCandidate() {
        assertEquals(0L, candidateRepository.count());
        candidateService.createCandidate("Precious", "Michael", "President");
        assertEquals(1L, candidateRepository.count());
    }
}
