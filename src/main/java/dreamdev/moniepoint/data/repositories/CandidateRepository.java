package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
}
