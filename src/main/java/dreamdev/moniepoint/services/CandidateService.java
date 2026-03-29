package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;

import java.util.List;

public interface CandidateService {
    CandidateResponse createCandidate(CandidateRequest candidateRequest);
    List<CandidateResponse> getAllCandidates();
    CandidateResponse getCandidate(String id);
    List<CandidateResponse> searchCandidates(String firstName, String lastName, String position);
}
