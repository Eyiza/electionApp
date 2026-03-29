package dreamdev.moniepoint.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@Document()
public class Voter {
    @Id
    private String id;
    private String name;
    private String nin;
    private Set<String> votedPositions = new HashSet<>();
}
