package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends MongoRepository<Position, String> {
    Optional<Position> findByTitleIgnoreCaseAndElectionId(String title, String electionId);
    List<Position> findByElectionId(String electionId);

}
