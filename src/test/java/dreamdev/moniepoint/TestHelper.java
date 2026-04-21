package dreamdev.moniepoint;

import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.services.ElectionService;
import dreamdev.moniepoint.services.PositionService;
import dreamdev.moniepoint.utils.ElectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

    @Autowired
    private ElectionService electionService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private PositionRepository positionRepository;

    // creates an ongoing election (started 1hr ago, ends in 5hrs)
    public Election createOngoingElection() {
        ElectionRequest request = new ElectionRequest();
        request.setTitle("Test Election");
        request.setStartDateTime(ElectionStatus.now().minusHours(1));
        request.setEndDateTime(ElectionStatus.now().plusHours(5));
        return electionRepository.save(buildElection(request));
    }

    // creates an upcoming election (starts in 1hr, ends in 5hrs)
    public Election createUpcomingElection() {
        ElectionRequest request = new ElectionRequest();
        request.setTitle("Test Election");
        request.setStartDateTime(ElectionStatus.now().plusHours(1));
        request.setEndDateTime(ElectionStatus.now().plusHours(5));
        return electionRepository.save(buildElection(request));
    }

    // creates an ended election (started 5hrs ago, ended 1hr ago)
    public Election createEndedElection() {
        ElectionRequest request = new ElectionRequest();
        request.setTitle("Test Election");
        request.setStartDateTime(ElectionStatus.now().minusHours(5));
        request.setEndDateTime(ElectionStatus.now().minusHours(1));
        return electionRepository.save(buildElection(request));
    }

    private Election buildElection(ElectionRequest request) {
        Election election = new Election();
        election.setTitle(request.getTitle());
        election.setStartDateTime(request.getStartDateTime());
        election.setEndDateTime(request.getEndDateTime());
        return election;
    }

    public Position createPosition(String title, String electionId) {
        PositionRequest request = new PositionRequest();
        request.setTitle(title);
        request.setElectionId(electionId);
        return positionRepository.save(buildPosition(request));
    }

    public CandidateRequest buildCandidateRequest(String firstName, String lastName, String positionId) {
        CandidateRequest request = new CandidateRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPositionId(positionId);
        return request;
    }

    public VoteRequest buildVoteRequest(String nin, String candidateId, String positionId) {
        VoteRequest request = new VoteRequest();
        request.setNin(nin);
        request.setCandidateId(candidateId);
        request.setPositionId(positionId);
        return request;
    }

    private Position buildPosition(PositionRequest request) {
        Position position = new Position();
        position.setTitle(request.getTitle());
        position.setElectionId(request.getElectionId());
        return position;
    }
}