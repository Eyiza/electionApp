package dreamdev.moniepoint.services;

public interface CandidateService {
    void createCandidate(String firstName, String lastName, String position);
    void getCandidate(String firstName, String lastName, String position);
    void voteCandidate(String id, String position);
}
