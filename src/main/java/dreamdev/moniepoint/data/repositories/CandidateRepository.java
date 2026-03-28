package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByFirstNameAndLastName(String firstName, String lastName);
    Optional<Candidate> findByFirstNameAndLastNameAndPosition(String firstName, String lastName, String position);
}
