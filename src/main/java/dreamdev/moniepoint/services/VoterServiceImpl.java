package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Voter;
import dreamdev.moniepoint.data.repositories.VoterRepository;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;
import dreamdev.moniepoint.exceptions.DuplicateVoterException;
import dreamdev.moniepoint.exceptions.InvalidVoterException;
import dreamdev.moniepoint.utils.Mapper;
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

    @Override
    public VoterResponse registerCandidate(VoterRequest voterRequest) {
        if (voterRepository.findByNin(voterRequest.getNin()).isPresent()) {
            throw new DuplicateVoterException("Voter with NIN " + voterRequest.getNin() + " is already registered");
        }
        Voter voter = map(voterRequest);
        Voter savedVoter = voterRepository.save(voter);
        return Mapper.map(savedVoter);
    }

    @Override
    public List<VoterResponse> getVoters() {
        List<Voter> savedVoters = voterRepository.findAll();
        List<VoterResponse> voterResponses = new ArrayList<>();
        for (Voter voter : savedVoters) voterResponses.add(Mapper.map(voter));
        return voterResponses;
    }

    @Override
    public VoterResponse getVoter(String id) {
        Optional<Voter> optionalVoter = voterRepository.findById(id);
        if (optionalVoter.isEmpty()) throw new InvalidVoterException("Voter with id " + id + " does not exist");
        return map(optionalVoter.get());
    }

    @Override
    public CandidateResponse voteCandidate(VoteRequest voteRequest) {
        return null;
    }
}
