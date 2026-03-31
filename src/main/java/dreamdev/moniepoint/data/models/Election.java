package dreamdev.moniepoint.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
public class Election {
    @Id
    private String id;
    private String name;
    private Date startDate;
    private Date endDate;
}
