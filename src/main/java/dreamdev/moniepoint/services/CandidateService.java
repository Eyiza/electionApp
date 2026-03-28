package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    void createCandidate(String firstName, String lastName, String position);
    List<Candidate> getAllCandidates();
    Candidate getCandidate(String id);
    Candidate getCandidate(String firstName, String lastName);
    void voteCandidate(String id, String position);
    void voteCandidate(String firstName, String lastName, String position);
}
