package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;

import java.util.List;

public class VoterServiceImpl implements VoterService {

    @Override
    public VoterResponse voteCandidate(VoterRequest voterRequest) {
        return null;
    }

    @Override
    public List<VoterResponse> getVoters() {
        return List.of();
    }

    @Override
    public CandidateResponse voteCandidate(VoteRequest voteRequest) {
        return null;
    }
}
