package dreamdev.moniepoint.dtos.requests;

import lombok.Data;

@Data
public class CandidateRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String position;

    public CandidateRequest() {}

    public CandidateRequest(String id){
        this.id = id;
    }

    public CandidateRequest(String id, String position) {
        this.id = id;
        this.position = position;
    }

    public CandidateRequest(String firstName, String lastName, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
    }
}
