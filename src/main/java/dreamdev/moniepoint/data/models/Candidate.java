package dreamdev.moniepoint.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Candidate {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String position;
    private int voteCount = 0;
}
