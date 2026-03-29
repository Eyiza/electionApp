package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Voter;
import dreamdev.moniepoint.data.repositories.CandidateRepository;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.VoteResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.AlreadyVotedException;
import dreamdev.moniepoint.exceptions.DuplicateVoterException;
import dreamdev.moniepoint.exceptions.InvalidCandidateIdException;
import dreamdev.moniepoint.exceptions.InvalidVoterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dreamdev.moniepoint.utils.Mapper.*;

@Service
public class VoterServiceImpl implements VoterService {
    @Autowired
    private VoterRepository voterRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public VoterResponse registerVoter(VoterRequest voterRequest) {
        if (voterRepository.findByNin(voterRequest.getNin()).isPresent()) {
            throw new DuplicateVoterException("Voter with NIN " + voterRequest.getNin() + " is already registered");
        }
        Voter voter = map(voterRequest);
        Voter savedVoter = voterRepository.save(voter);
        return map(savedVoter);
    }

    @Override
    public List<VoterResponse> getVoters() {
        List<Voter> savedVoters = voterRepository.findAll();
        List<VoterResponse> voterResponses = new ArrayList<>();
        for (Voter voter : savedVoters) voterResponses.add(map(voter));
        return voterResponses;
    }

    @Override
    public VoterResponse getVoter(String id) {
        Optional<Voter> optionalVoter = voterRepository.findById(id);
        if (optionalVoter.isEmpty()) throw new InvalidVoterException("Voter with id " + id + " does not exist");
        return map(optionalVoter.get());
    }

    @Override
    public VoteResponse voteCandidate(VoteRequest voteRequest) {
        Optional<Voter> optionalVoter = voterRepository.findByNin(voteRequest.getNin());
        if (optionalVoter.isEmpty()) throw new InvalidVoterException("Voter with NIN " + voteRequest.getNin() + " is not registered");
        Voter voter = optionalVoter.get();
        if (voter.getVotedPositions().contains(voteRequest.getCandidatePosition())) {
            throw new AlreadyVotedException("You have already voted for " + voteRequest.getCandidatePosition() + " position");
        }

        Optional<Candidate> optionalCandidate = candidateRepository.findById(voteRequest.getCandidateId());
        if (optionalCandidate.isEmpty()) throw new InvalidCandidateIdException("Candidate not found");
        Candidate candidate = optionalCandidate.get();
        if (!candidate.getPosition().equals(voteRequest.getCandidatePosition())) {
            throw new InvalidCandidateIdException("Candidate is not running for position: " + voteRequest.getCandidatePosition());
        }

        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);

        voter.getVotedPositions().add(voteRequest.getCandidatePosition());
        voterRepository.save(voter);

        return map(voter, candidate.getFirstName() + " " + candidate.getLastName());
    }
}
