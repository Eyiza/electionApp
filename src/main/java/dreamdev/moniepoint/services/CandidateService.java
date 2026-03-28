package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;

import java.util.List;

public interface CandidateService {
    void createCandidate(String firstName, String lastName, String position);
    List<Candidate> getAllCandidates();
    Candidate getCandidate(String id);
    void voteCandidate(String id, String position);
}
