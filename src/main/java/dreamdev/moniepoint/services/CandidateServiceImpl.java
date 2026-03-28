package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public void createCandidate(String firstName, String lastName, String position){
        if (candidateRepository.findByFirstNameAndLastName(firstName, lastName).isPresent()){
            throw new DuplicateCandidateException("Candidate already exists");
        }
        Candidate candidate = new Candidate();
        candidate.setFirstName(firstName);
        candidate.setLastName(lastName);
        candidate.setPosition(position);
        candidateRepository.save(candidate);
    }

    @Override
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    @Override
    public Candidate getCandidate(String id) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate with id " + id + " does not exist");
        Candidate candidate = optionalCandidate.get();
        return candidate;
    }

    @Override
    public Candidate getCandidate(String firstName, String lastName) {
        Optional<Candidate> optionalCandidate = candidateRepository.findByFirstNameAndLastName(firstName, lastName);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate" + firstName + " " + lastName + " does not exist");
        return optionalCandidate.get();
    }

    @Override
    public void voteCandidate(String id, String position) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate with id " + id + " does not exist");
        Candidate candidate = optionalCandidate.get();
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);
    }

    @Override
    public void voteCandidate(String firstName, String lastName, String position) {
        Optional<Candidate> optionalCandidate = candidateRepository.findByFirstNameAndLastName(firstName, lastName);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate" + firstName + " " + lastName + " does not exist");
        Candidate candidate = optionalCandidate.get();
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);
    }

}
