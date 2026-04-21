package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.exceptions.*;
import dreamdev.moniepoint.utils.ElectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static dreamdev.moniepoint.utils.Mapper.*;

@Service
public class CandidateServiceImpl implements CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Override
    public CandidateResponse createCandidate(CandidateRequest candidateRequest){
        Position position = getPositionIfElectionIsUpcoming(candidateRequest.getPositionId());
        duplicationCheck(candidateRequest);
        Candidate candidate = map(candidateRequest);
        Candidate savedCandidate = candidateRepository.save(candidate);
        return map(savedCandidate, position);
    }

    private void duplicationCheck(CandidateRequest candidateRequest) {
        String firstName = candidateRequest.getFirstName();
        String lastName = candidateRequest.getLastName();
        String position = candidateRequest.getPositionId();
        if (candidateRepository.findByFirstNameAndLastNameAndPositionId(firstName, lastName, position).isPresent()){
            throw new DuplicateCandidateException("Candidate already exists");
        }
    }

    private Position getPositionIfElectionIsUpcoming(String positionId) {
        Optional<Position> optionalPosition = positionRepository.findById(positionId);
        if (optionalPosition.isEmpty()) throw new InvalidPositionException("Position not found");

        Position position = optionalPosition.get();
        Optional<Election> optionalElection = electionRepository.findById(position.getElectionId());
        if (optionalElection.isEmpty()) throw new InvalidElectionException("Election not found");

        Election election = optionalElection.get();
        if (!ElectionStatus.isUpcoming(election)) {
            throw new ElectionNotActiveException("Candidates can only be registered before the election starts");
        }

        return position;
    }

    @Override
    public List<CandidateResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        List<CandidateResponse> candidateResponses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Position position = positionRepository.findById(candidate.getPositionId()).get();
            candidateResponses.add(map(candidate, position));
        }
        return candidateResponses;
    }

    @Override
    public CandidateResponse getCandidate(String id) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
        if(optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate with id " + id + " does not exist");
        Candidate candidate = optionalCandidate.get();
        Position position = positionRepository.findById(candidate.getPositionId()).get();
        return map(candidate, position);
    }

    @Override
    public List<CandidateResponse> searchCandidates(String firstName, String lastName) {
        List<Candidate> candidates = candidateRepository.searchByFields(firstName, lastName);
        List<CandidateResponse> candidateResponses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Position position = positionRepository.findById(candidate.getPositionId()).get();
            candidateResponses.add(map(candidate, position));
        }
        return candidateResponses;
    }

    @Override
    public Map<String, List<CandidateResponse>> getResults() {
        List<Candidate> candidates = candidateRepository.findAllByOrderByVoteCountDesc();
        Map<String, List<CandidateResponse>> results = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            Position position = positionRepository.findById(candidate.getPositionId()).get();
            String positionTitle = position.getTitle();
            List<CandidateResponse> positionsResult = results.get(positionTitle);
            if (positionsResult == null) {
                positionsResult = new ArrayList<>();
                results.put(positionTitle, positionsResult);
            }
            positionsResult.add(map(candidate));
        }
        return results;
    }

    @Override
    public List<CandidateResponse> getResultsByPosition(String positionId) {
        List<Candidate> candidates = candidateRepository.findByPositionIdOrderByVoteCountDesc(positionId);
        List<CandidateResponse> responses = new ArrayList<>();
        for (Candidate candidate : candidates) {
            Position position = positionRepository.findById(candidate.getPositionId()).get();
            responses.add(map(candidate, position));
        }
        return responses;
    }


}
