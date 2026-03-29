package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.DuplicateVoterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VoterServiceImplTest {
    @Autowired
    private VoterRepository voterRepository;
    @Autowired
    private VoterService voterService;

    private VoterRequest voterJamie = new VoterRequest();
    private VoterRequest voterCarrie = new VoterRequest();

    @BeforeEach
    void setUp() {
        voterRepository.deleteAll();
        voterJamie.setName("Jamie");
        voterJamie.setNin("4567");
        voterCarrie.setName("Carrie");
        voterCarrie.setNin("6789");
    }

    @Test
    public void voterRepositoryIsEmpty_Test(){
        assertEquals(0L, voterRepository.count());
    }

    @Test
    public void registerVoter_countIsOneTest(){
        assertEquals(0L, voterRepository.count());
        VoterResponse voterResponse = voterService.registerCandidate(voterJamie);
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void registerVoter_whenVoterExists_Test(){
        voterService.registerCandidate(voterCarrie);
        assertThrows(DuplicateVoterException.class, ()-> voterService.registerCandidate(voterCarrie));
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void getAllVoters_emptyListTest() {
        List<VoterResponse> voters = voterService.getVoters();
        assertTrue(voters.isEmpty());
    }

    @Test
    public void registerTwoVoters_countIsTwoTest(){
        voterService.registerCandidate(voterJamie);
        voterService.registerCandidate(voterCarrie);
        List<VoterResponse> voters = voterService.getVoters();

        assertEquals(2, voters.size());
    }
}