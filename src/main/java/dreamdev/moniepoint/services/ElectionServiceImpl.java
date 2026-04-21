package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.responses.ElectionResponse;
import dreamdev.moniepoint.exceptions.InvalidElectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dreamdev.moniepoint.utils.Mapper.map;

@Service
public class ElectionServiceImpl implements ElectionService {
    @Autowired
    private ElectionRepository electionRepository;

    @Override
    public ElectionResponse createElection(ElectionRequest electionRequest) {
        LocalDateTime startDateTime = electionRequest.getStartDateTime();
        LocalDateTime endDateTime = electionRequest.getEndDateTime();
        if (endDateTime.isBefore(startDateTime)) {
            throw new InvalidElectionException("End date must be after start date");
        }
        Election election = map(electionRequest);
        Election savedElection = electionRepository.save(election);
        return map(savedElection);
    }

    @Override
    public ElectionResponse getElection(String id) {
        Optional<Election> optionalElection = electionRepository.findById(id);
        if (optionalElection.isEmpty()) throw new InvalidElectionException("Specified Election does not exist");
        return map(optionalElection.get());
    }

    @Override
    public List<ElectionResponse> getAllElections() {
        List<Election> elections = electionRepository.findAll();
        List<ElectionResponse> electionResponses = new ArrayList<>();
        for (Election election : elections) {
            electionResponses.add(map(election));
        }
        return electionResponses;
    }
}
