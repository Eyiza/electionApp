package dreamdev.moniepoint.data.repositories;

import dreamdev.moniepoint.data.models.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends MongoRepository<Candidate, String> {
    @Query("{ 'firstName': { $regex: '^?0$', $options: 'i' }, 'lastName': { $regex: '^?1$', $options: 'i' }, 'positionId': '?2' }")
    Optional<Candidate> findByFirstNameAndLastNameAndPositionId(String firstName, String lastName, String positionId);

    @Query("{ 'firstName': { $regex: ?0, $options: 'i' }, 'lastName': { $regex: ?1, $options: 'i' } }")
    List<Candidate> searchByFields(String firstName, String lastName);

    List<Candidate> findByPositionIdOrderByVoteCountDesc(String positionId);
    List<Candidate> findAllByOrderByVoteCountDesc();
}
