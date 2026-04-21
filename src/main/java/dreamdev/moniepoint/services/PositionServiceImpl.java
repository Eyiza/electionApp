package dreamdev.moniepoint.services;

import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.repositories.ElectionRepository;
import dreamdev.moniepoint.data.repositories.PositionRepository;
import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.responses.PositionResponse;
import dreamdev.moniepoint.exceptions.DuplicatePositionException;
import dreamdev.moniepoint.exceptions.ElectionNotActiveException;
import dreamdev.moniepoint.exceptions.InvalidElectionException;
import dreamdev.moniepoint.exceptions.InvalidPositionException;
import dreamdev.moniepoint.utils.ElectionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dreamdev.moniepoint.utils.Mapper.map;

@Service
public class PositionServiceImpl implements PositionService {
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private ElectionRepository electionRepository;

    @Override
    public PositionResponse createPosition(PositionRequest positionRequest) {
        Optional<Election> optionalElection = electionRepository.findById(positionRequest.getElectionId());
        if (optionalElection.isEmpty()) throw new InvalidElectionException("Election not found");

        Election election = optionalElection.get();
        if (!ElectionStatus.isUpcoming(election)) {
            throw new ElectionNotActiveException("Positions can only be added before the election starts");
        }

        Optional<Position> existingPosition = positionRepository.findByTitleIgnoreCaseAndElectionId(positionRequest.getTitle(), positionRequest.getElectionId());
        if (existingPosition.isPresent()) throw new DuplicatePositionException("Position already exists in this election");

        Position position = map(positionRequest);
        Position savedPosition = positionRepository.save(position);

        election.getPositionIds().add(savedPosition.getId());
        electionRepository.save(election);

        return map(savedPosition);
    }

    @Override
    public PositionResponse getPosition(String id) {
        Optional<Position> optionalPosition = positionRepository.findById(id);
        if (optionalPosition.isEmpty()) throw new InvalidPositionException("Specified position does not exist");
        return map(optionalPosition.get());
    }

    @Override
    public List<PositionResponse> getPositionsByElection(String electionId) {
        if (!electionRepository.existsById(electionId)) throw new InvalidElectionException("Election not found");
        List<Position> positions = positionRepository.findByElectionId(electionId);
        List<PositionResponse> positionResponses = new ArrayList<>();
        for (Position position : positions) {
            positionResponses.add(map(position));
        }
        return positionResponses;
    }
}
