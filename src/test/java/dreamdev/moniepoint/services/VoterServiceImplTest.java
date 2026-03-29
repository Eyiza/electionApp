package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoteResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.AlreadyVotedException;
import dreamdev.moniepoint.exceptions.DuplicateVoterException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import dreamdev.moniepoint.exceptions.InvalidVoterException;
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
    private CandidateRepository candidateRepository;
    @Autowired
    private VoterService voterService;
    @Autowired
    private CandidateService candidateService;

    private VoterRequest voterJamie;
    private VoterRequest voterCarrie;

    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

    @BeforeEach
    void setUp() {
        voterRepository.deleteAll();
        candidateRepository.deleteAll();

        voterJamie = new VoterRequest();
        voterJamie.setName("Jamie");
        voterJamie.setNin("4567");

        voterCarrie = new VoterRequest();
        voterCarrie.setName("Carrie");
        voterCarrie.setNin("6789");

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
    public void voterRepositoryIsEmpty_Test(){
        assertEquals(0L, voterRepository.count());
    }

    @Test
    public void registerVoter_countIsOneTest(){
        assertEquals(0L, voterRepository.count());
        VoterResponse voterResponse = voterService.registerVoter(voterJamie);
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void registerVoter_whenVoterExists_Test(){
        voterService.registerVoter(voterCarrie);
        assertThrows(DuplicateVoterException.class, ()-> voterService.registerVoter(voterCarrie));
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void getAllVoters_emptyListTest() {
        List<VoterResponse> voters = voterService.getVoters();
        assertTrue(voters.isEmpty());
    }

    @Test
    public void registerTwoVoters_countIsTwoTest(){
        voterService.registerVoter(voterJamie);
        voterService.registerVoter(voterCarrie);
        List<VoterResponse> voters = voterService.getVoters();

        assertEquals(2, voters.size());
    }

    @Test
    public void getVoterById_Test() {
        VoterResponse savedVoter = voterService.registerVoter(voterJamie);
        VoterResponse voter = voterService.getVoter(savedVoter.getId());
        assertEquals("Jamie", voter.getName());
        assertEquals("4567", voter.getNin());
    }

    @Test
    public void getVoterByInvalidId_throwExceptionTest() {
        voterService.registerVoter(voterJamie);
        assertThrows(InvalidVoterException.class, ()-> voterService.getVoter("Invalid"));
    }

    private VoteRequest buildVoteRequest(String nin, String candidateId, String position) {
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setNin(nin);
        voteRequest.setCandidateId(candidateId);
        voteRequest.setCandidatePosition(position);
        return voteRequest;
    }

    @Test
    public void voteCandidate_isSuccessfulTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse candidate = candidateService.createCandidate(candidatePrecious);
        VoteRequest voteRequest = buildVoteRequest(voter.getNin(), candidate.getId(), "President");
        VoteResponse voteResponse = voterService.voteCandidate(voteRequest);

        CandidateResponse saved = candidateService.getCandidate(candidate.getId());
        assertEquals(1, saved.getVoteCount());
    }

    @Test
    public void voteCandidate_recordsPositionOnVoterTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse candidate = candidateService.createCandidate(candidatePrecious);
        VoteRequest voteRequest = buildVoteRequest(voter.getNin(), candidate.getId(), "President");
        VoteResponse voteResponse = voterService.voteCandidate(voteRequest);

        assertTrue(voteResponse.getVotedPositions().contains("President"));
    }

    @Test
    public void voteCandidate_onDifferentPositionsTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);

        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);
        candidateJohn.setPosition("Governor");
        CandidateResponse john = candidateService.createCandidate(candidateJohn);

        voterService.voteCandidate(buildVoteRequest(voter.getNin(), precious.getId(), "President"));
        VoteResponse voteResponse = voterService.voteCandidate(buildVoteRequest(voter.getNin(), john.getId(), "Governor"));

        assertEquals(2, voteResponse.getVotedPositions().size());
        assertTrue(voteResponse.getVotedPositions().contains("President"));
        assertTrue(voteResponse.getVotedPositions().contains("Governor"));

        precious = candidateService.getCandidate(precious.getId());
        assertEquals(1, precious.getVoteCount());

        john = candidateService.getCandidate(john.getId());
        assertEquals(1, john.getVoteCount());
    }

    @Test
    public void voteWithUnregisteredNin_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);
        VoteRequest voteRequest = buildVoteRequest("0000", precious.getId(), "President");
        assertThrows(InvalidVoterException.class, () -> voterService.voteCandidate(voteRequest));
    }

    @Test
    public void voteCandidate_whenVoterAlreadyVotedForPosition_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        VoteRequest voteRequest = buildVoteRequest(voter.getNin(), precious.getId(), "President");
        voterService.voteCandidate(voteRequest);
        assertThrows(AlreadyVotedException.class, () -> voterService.voteCandidate(voteRequest));
        precious = candidateService.getCandidate(precious.getId());
        assertEquals(1, precious.getVoteCount());
    }

    @Test
    public void voteCandidate_withInvalidCandidateId_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        VoteRequest voteRequest = buildVoteRequest(voter.getNin(), "invalid", "President");
        assertThrows(InvalidCandidateIdException.class, () -> voterService.voteCandidate(voteRequest));
    }

    @Test
    public void voteCandidate_whenPositionIsWrong_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        VoteRequest voteRequest = buildVoteRequest(voter.getNin(), precious.getId(), "Governor");
        assertThrows(InvalidCandidateIdException.class, () -> voterService.voteCandidate(voteRequest));
        precious = candidateService.getCandidate(precious.getId());
        assertEquals(0, precious.getVoteCount());
    }
}