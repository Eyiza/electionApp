package dreamdev.moniepoint.dtos.responses;

import lombok.Data;

@Data
public class CandidateResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String position;
    private int voteCount;
}
