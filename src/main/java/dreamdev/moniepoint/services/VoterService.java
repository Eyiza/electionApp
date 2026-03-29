package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.VoteResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;

import java.util.List;

public interface VoterService {
    VoterResponse registerVoter(VoterRequest voterRequest);
    List<VoterResponse> getVoters();
    VoterResponse getVoter(String id);
    VoteResponse voteCandidate(VoteRequest voteRequest);

}
