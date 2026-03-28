package dreamdev.moniepoint.utils;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.CandidateCreationResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;

public class Mapper {
    public static Candidate map(CandidateRequest candidateRequest) {
        Candidate candidate = new Candidate();
        candidate.setFirstName(candidateRequest.getFirstName());
        candidate.setLastName(candidateRequest.getLastName());
        candidate.setPosition(candidateRequest.getPosition());
        return candidate;
    }

    public static CandidateCreationResponse mapToCreationResponse(Candidate candidate) {
        CandidateCreationResponse candidateResponse = new CandidateCreationResponse();
        candidateResponse.setFirstName(candidate.getFirstName());
        candidateResponse.setLastName(candidate.getLastName());
        candidateResponse.setPosition(candidate.getPosition());
        return candidateResponse;
    }

    public static CandidateResponse map(Candidate candidate) {
        CandidateResponse candidateResponse = new CandidateResponse();
        candidateResponse.setId(candidate.getId());
        candidateResponse.setFirstName(candidate.getFirstName());
        candidateResponse.setLastName(candidate.getLastName());
        candidateResponse.setPosition(candidate.getPosition());
        candidateResponse.setVoteCount(candidate.getVoteCount());
        return candidateResponse;
    }
}
