package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Election;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ElectionRepository extends MongoRepository<Election, String> {

}
