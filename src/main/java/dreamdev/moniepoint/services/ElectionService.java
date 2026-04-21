package dreamdev.moniepoint.services;

import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.responses.ElectionResponse;

import java.util.List;

public interface ElectionService {
    ElectionResponse createElection(ElectionRequest electionRequest);
    ElectionResponse getElection(String id);
    List<ElectionResponse> getAllElections();
}
