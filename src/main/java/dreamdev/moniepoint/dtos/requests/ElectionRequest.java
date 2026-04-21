package dreamdev.moniepoint.dtos.requests;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElectionRequest {
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
