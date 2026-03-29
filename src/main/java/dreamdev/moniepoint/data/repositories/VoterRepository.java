package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Voter;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface VoterRepository extends MongoRepository<Voter, String> {
    Optional<Voter> findByNin(String nin);
}
