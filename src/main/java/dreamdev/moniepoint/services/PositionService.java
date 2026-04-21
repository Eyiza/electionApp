package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.responses.PositionResponse;

import java.util.List;

public interface PositionService {
    PositionResponse createPosition(PositionRequest positionRequest);
    PositionResponse getPosition(String id);
    List<PositionResponse> getPositionsByElection(String electionId);
}
