package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateCreationResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dreamdev.moniepoint.utils.Mapper.*;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public CandidateCreationResponse createCandidate(CandidateRequest candidateRequest){
        duplicationCheck(candidateRequest);
        Candidate candidate = map(candidateRequest);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return mapToCreationResponse(savedCandidate);
    }

    private void duplicationCheck(CandidateRequest candidateRequest) {
        String firstName = candidateRequest.getFirstName();
        String lastName = candidateRequest.getLastName();
        if (candidateRepository.findByFirstNameAndLastName(firstName, lastName).isPresent()){
            throw new DuplicateCandidateException("Candidate already exists");
        }

    }

    @Override
    public List<CandidateResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        List<CandidateResponse> candidateResponses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            candidateResponses.add(map(candidate));
        }
        return candidateResponses;
    }

    @Override
    public CandidateResponse getCandidate(String id) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate with id " + id + " does not exist");
        CandidateResponse candidate = map(optionalCandidate.get());
        return candidate;
    }

    @Override
    public CandidateResponse getCandidate(CandidateRequest candidateRequest) {
        Optional<Candidate> optionalCandidate = getCandidateIfItExists(candidateRequest);
        return map(optionalCandidate.get());
    }

    private @NonNull Optional<Candidate> getCandidateIfItExists(CandidateRequest candidateRequest) {
        String firstName = candidateRequest.getFirstName();
        String lastName = candidateRequest.getLastName();
        Optional<Candidate> optionalCandidate = candidateRepository.findByFirstNameAndLastName(firstName, lastName);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate" + firstName + " " + lastName + " does not exist");
        return optionalCandidate;
    }

    @Override
    public CandidateResponse voteCandidate(String id, String position) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate with id " + id + " does not exist");
        Candidate candidate = optionalCandidate.get();
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);
    }

    @Override
    public CandidateResponse voteCandidate(CandidateRequest candidateRequest) {
        Optional<Candidate> optionalCandidate = getCandidateIfItExists(candidateRequest);
        Candidate candidate = optionalCandidate.get();
        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);
    }

}
