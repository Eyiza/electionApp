package dreamdev.moniepoint.dtos.responses;

import lombok.Data;

import java.util.Set;

@Data
public class VoteResponse {
    private String name;
    private Set<String> votes;
}
