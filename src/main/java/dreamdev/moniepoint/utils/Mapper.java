package dreamdev.moniepoint.utils;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Voter;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.CandidateCreationResponse;
import dreamdev.moniepoint.dtos.responses.CandidateResponse;
import dreamdev.moniepoint.dtos.responses.VoterResponse;

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

    public static Voter map(VoterRequest voterRequest) {
        Voter voter = new Voter();
        voter.setName(voterRequest.getName());
        voter.setNin(voterRequest.getNin());
        return voter;
    }

    public static VoterResponse mapToVoterResponse(Voter voter) {
        VoterResponse response = new VoterResponse();
        response.setId(voter.getId());
        response.setName(voter.getName());
        response.setNin(voter.getNin());
        response.setVotedPositions(voter.getVotedPositions());
        return response;
    }
}
