package dreamdev.moniepoint.data.models;

import org.springframework.data.annotation.Id;

import java.util.HashSet;
import java.util.Set;

public class Voter {
    @Id
    private String id;
    private String name;
    private Set<String> votedPositions = new HashSet<>();
}
