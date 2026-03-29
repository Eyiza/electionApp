package dreamdev.moniepoint.dtos.responses;

import lombok.Data;

import java.util.Set;

@Data
public class VoterResponse {
    private String id;
    private String name;
    private String nin;
    private Set<String> votedPositions;
}
