package dreamdev.moniepoint.dtos.responses;

import lombok.Data;

@Data
public class CandidateCreationResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String position;
}
