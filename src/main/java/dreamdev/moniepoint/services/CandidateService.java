package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;

import java.util.List;
import java.util.Optional;

public interface CandidateService {
    void createCandidate(String firstName, String lastName, String position);
    List<Candidate> getAllCandidates();
    Candidate getCandidateById(String id);
//    Optional<Candidate> findByFirstAndLastName(String firstName, String lastName);
    void voteCandidate(String id, String position);
}
