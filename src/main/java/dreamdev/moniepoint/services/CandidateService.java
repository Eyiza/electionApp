package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateCreationResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;

import java.util.List;

public interface CandidateService {
    CandidateCreationResponse createCandidate(CandidateRequest candidateRequest);
    List<CandidateResponse> getAllCandidates();
    CandidateResponse getCandidate(String id);
    CandidateResponse getCandidate(CandidateRequest candidateRequest);
    CandidateResponse voteCandidate(String id, String position);
    CandidateResponse voteCandidate(CandidateRequest candidateRequest);
}
