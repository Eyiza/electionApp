package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public void createCandidate(String firstName, String lastName, String position){
        Candidate candidate = new Candidate();
        candidate.setFirstName(firstName);
        candidate.setLastName(lastName);
        candidate.setPosition(position);
        candidateRepository.save(candidate);
    }

    @Override
    public void getCandidate(String firstName, String lastName, String position) {

    }

    @Override
    public void voteCandidate(String id, String position) {

    }
}
