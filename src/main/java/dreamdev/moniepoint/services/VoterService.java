package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;

import java.util.List;

public interface VoterService {
    VoterResponse voteCandidate(VoterRequest voterRequest);
    List<VoterResponse> getVoters();
    CandidateResponse voteCandidate(VoteRequest voteRequest);
}
