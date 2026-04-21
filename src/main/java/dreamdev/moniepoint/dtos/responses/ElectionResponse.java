package dreamdev.moniepoint.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ElectionResponse {
    private String id;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String status;
    private List<String> positionIds;
}
