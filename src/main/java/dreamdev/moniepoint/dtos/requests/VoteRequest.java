package dreamdev.moniepoint.dtos.requests;

import lombok.Data;

@Data
public class VoteRequest {
    private String nin;
    private String candidateId;
    private String candidatePosition;
}
