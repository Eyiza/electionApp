package dreamdev.moniepoint.dtos.requests;

import lombok.Data;

@Data
public class CandidateCreationRequest {
    private String firstName;
    private String lastName;
    private String position;
}
