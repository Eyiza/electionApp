package dreamdev.moniepoint.services;

import dreamdev.moniepoint.TestHelper;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.VoteResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.*;
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
    private ElectionRepository electionRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private VoterService voterService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private TestHelper testHelper;

    private VoterRequest voterJamie;
    private VoterRequest voterCarrie;
    private Election ongoingElection;
    private Position presidentPosition;
    private Position governorPosition;
    private CandidateRequest candidatePrecious;
    private CandidateRequest candidateJohn;

    @BeforeEach
    void setUp() {
        voterRepository.deleteAll();
        candidateRepository.deleteAll();
        positionRepository.deleteAll();
        electionRepository.deleteAll();

        voterJamie = new VoterRequest();
        voterJamie.setName("Jamie");
        voterJamie.setNin("4567");

        voterCarrie = new VoterRequest();
        voterCarrie.setName("Carrie");
        voterCarrie.setNin("6789");

        // candidates are registered before election starts (upcoming)
        Election upcomingElection = testHelper.createUpcomingElection();
        presidentPosition = testHelper.createPosition("President", upcomingElection.getId());
        governorPosition = testHelper.createPosition("Governor", upcomingElection.getId());

        candidatePrecious = testHelper.buildCandidateRequest("Precious", "Michael", presidentPosition.getId());
        candidateJohn = testHelper.buildCandidateRequest("John", "Doe", governorPosition.getId());

        // now make the election ongoing so votes can be cast
        upcomingElection.setStartDateTime(upcomingElection.getStartDateTime().minusHours(2));
        electionRepository.save(upcomingElection);

        ongoingElection = electionRepository.findById(upcomingElection.getId()).get();
    }

    @Test
    public void voterRepositoryIsEmpty_Test() {
        assertEquals(0L, voterRepository.count());
    }

    @Test
    public void registerVoter_countIsOneTest() {
        voterService.registerVoter(voterJamie);
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void registerVoter_whenVoterExists_Test() {
        voterService.registerVoter(voterCarrie);
        assertThrows(DuplicateVoterException.class, () -> voterService.registerVoter(voterCarrie));
        assertEquals(1L, voterRepository.count());
    }

    @Test
    public void getAllVoters_emptyListTest() {
        List<VoterResponse> voters = voterService.getVoters();
        assertTrue(voters.isEmpty());
    }

    @Test
    public void registerTwoVoters_countIsTwoTest() {
        voterService.registerVoter(voterJamie);
        voterService.registerVoter(voterCarrie);
        assertEquals(2, voterService.getVoters().size());
    }

    @Test
    public void getVoterById_Test() {
        VoterResponse saved = voterService.registerVoter(voterJamie);
        VoterResponse found = voterService.getVoter(saved.getId());
        assertEquals("Jamie", found.getName());
        assertEquals("4567", found.getNin());
    }

    @Test
    public void getVoterByInvalidId_throwExceptionTest() {
        voterService.registerVoter(voterJamie);
        assertThrows(InvalidVoterException.class, () -> voterService.getVoter("Invalid"));
    }

    @Test
    public void voteCandidate_isSuccessfulTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId()));

        CandidateResponse saved = candidateService.getCandidate(precious.getId());
        assertEquals(1, saved.getVoteCount());
    }

    @Test
    public void voteCandidate_recordsPositionOnVoterTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        VoteResponse response = voterService.voteCandidate(
                testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId()));

        assertTrue(response.getVotedPositions().contains(presidentPosition.getId()));
    }

    @Test
    public void voteCandidate_onDifferentPositionsTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);
        CandidateResponse john = candidateService.createCandidate(candidateJohn);

        voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId()));
        VoteResponse response = voterService.voteCandidate(
                testHelper.buildVoteRequest(voter.getNin(), john.getId(), governorPosition.getId()));

        assertEquals(2, response.getVotedPositions().size());
        assertTrue(response.getVotedPositions().contains(presidentPosition.getId()));
        assertTrue(response.getVotedPositions().contains(governorPosition.getId()));
    }

    @Test
    public void voteWithUnregisteredNin_throwsExceptionTest() {
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);
        assertThrows(InvalidVoterException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest("0000", precious.getId(), presidentPosition.getId())));
    }

    @Test
    public void voteCandidate_whenVoterAlreadyVotedForPosition_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId()));
        assertThrows(AlreadyVotedException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId())));

        assertEquals(1, candidateService.getCandidate(precious.getId()).getVoteCount());
    }

    @Test
    public void voteCandidate_withInvalidCandidateId_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        assertThrows(InvalidCandidateIdException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), "invalid", presidentPosition.getId())));
    }

    @Test
    public void voteCandidate_whenPositionIsWrong_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        // precious is running for president, but we pass governorPosition
        assertThrows(InvalidCandidateIdException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), governorPosition.getId())));

        assertEquals(0, candidateService.getCandidate(precious.getId()).getVoteCount());
    }

    @Test
    public void voteCandidate_whenElectionHasEnded_throwsExceptionTest() {
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse precious = candidateService.createCandidate(candidatePrecious);

        // push end time to the past
        ongoingElection.setEndDateTime(ongoingElection.getEndDateTime().minusHours(10));
        electionRepository.save(ongoingElection);

        assertThrows(ElectionNotActiveException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), precious.getId(), presidentPosition.getId())));
    }

    @Test
    public void voteCandidate_whenElectionHasNotStarted_throwsExceptionTest() {
        Election upcomingElection = testHelper.createUpcomingElection();
        Position position = testHelper.createPosition("Senator", upcomingElection.getId());
        CandidateRequest request = testHelper.buildCandidateRequest("Jane", "Smith", position.getId());

        // save candidate directly — bypassing the upcoming-only check
        candidateService.createCandidate(request);
        VoterResponse voter = voterService.registerVoter(voterJamie);
        CandidateResponse jane = candidateService.getAllCandidates().stream()
                .filter(c -> c.getFirstName().equals("Jane"))
                .findFirst().get();

        assertThrows(ElectionNotActiveException.class, () ->
                voterService.voteCandidate(testHelper.buildVoteRequest(voter.getNin(), jane.getId(), position.getId())));
    }
}