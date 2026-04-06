package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.exceptions.DuplicateCandidateException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static dreamdev.moniepoint.utils.Mapper.*;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public CandidateResponse createCandidate(CandidateRequest candidateRequest){
        duplicationCheck(candidateRequest);
        Candidate candidate = map(candidateRequest);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return map(savedCandidate);
    }

    private void duplicationCheck(CandidateRequest candidateRequest) {
        String firstName = candidateRequest.getFirstName();
        String lastName = candidateRequest.getLastName();
        String position = candidateRequest.getPosition();
        if (candidateRepository.findByFirstNameAndLastNameAndPosition(firstName, lastName, position).isPresent()){
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
        return map(optionalCandidate.get());
    }

    @Override
    public List<CandidateResponse> searchCandidates(String firstName, String lastName, String position) {
        List<Candidate> candidates = candidateRepository.searchByFields(firstName, lastName, position);
        List<CandidateResponse> candidateResponses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            candidateResponses.add(map(candidate));
        }
        return candidateResponses;
    }

    @Override
    public Map<String, List<CandidateResponse>> getResults() {
        List<Candidate> candidates = candidateRepository.findAllByOrderByVoteCountDesc();
        Map<String, List<CandidateResponse>> results = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            String position = candidate.getPosition();
            List<CandidateResponse> positionsResult = results.get(position);
            if (positionsResult == null) {
                positionsResult = new ArrayList<>();
                results.put(position, positionsResult);
            }
            positionsResult.add(map(candidate));
        }
        return results;
    }

    @Override
    public List<CandidateResponse> getResultsByPosition(String position) {
        List<Candidate> candidates = candidateRepository.findByPositionIgnoreCaseOrderByVoteCountDesc(position);
        System.out.println(candidates.toString());
        List<CandidateResponse> responses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            responses.add(map(candidate));
        }
        return responses;
    }


}
