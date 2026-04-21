package dreamdev.moniepoint.utils;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.data.models.Election;
import dreamdev.moniepoint.data.models.Position;
import dreamdev.moniepoint.data.models.Voter;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.dtos.responses.*;

public class Mapper {
    public static Candidate map(CandidateRequest candidateRequest) {
        Candidate candidate = new Candidate();
        candidate.setFirstName(candidateRequest.getFirstName());
        candidate.setLastName(candidateRequest.getLastName());
        candidate.setPositionId(candidateRequest.getPositionId());
        return candidate;
    }

    public static CandidateResponse map(Candidate candidate) {
        CandidateResponse candidateResponse = new CandidateResponse();
        candidateResponse.setId(candidate.getId());
        candidateResponse.setFirstName(candidate.getFirstName());
        candidateResponse.setLastName(candidate.getLastName());
        candidateResponse.setPositionId(candidate.getPositionId());
        candidateResponse.setVoteCount(candidate.getVoteCount());
        return candidateResponse;
    }

    public static CandidateResponse map(Candidate candidate, Position position) {
        CandidateResponse candidateResponse = new CandidateResponse();
        candidateResponse.setId(candidate.getId());
        candidateResponse.setFirstName(candidate.getFirstName());
        candidateResponse.setLastName(candidate.getLastName());
        candidateResponse.setPositionId(candidate.getPositionId());
        candidateResponse.setPositionTitle(position.getTitle());
        candidateResponse.setVoteCount(candidate.getVoteCount());
        return candidateResponse;
    }

    public static Voter map(VoterRequest voterRequest) {
        Voter voter = new Voter();
        voter.setName(voterRequest.getName());
        voter.setNin(voterRequest.getNin());
        return voter;
    }

    public static VoterResponse map(Voter voter) {
        VoterResponse response = new VoterResponse();
        response.setId(voter.getId());
        response.setName(voter.getName());
        response.setNin(voter.getNin());
        response.setVotedPositions(voter.getVotedPositions());
        return response;
    }

    public static VoteResponse map(Voter voter, String candidateName) {
        VoteResponse response = new VoteResponse();
        response.setName(voter.getName());
        response.setVotedPositions(voter.getVotedPositions());
        response.setCandidateName(candidateName);
        return response;
    }

    public static Election map(ElectionRequest electionRequest) {
        Election election = new Election();
        election.setTitle(electionRequest.getTitle());
        election.setStartDateTime(electionRequest.getStartDateTime());
        election.setEndDateTime(electionRequest.getEndDateTime());
        return election;
    }

    public static ElectionResponse map(Election election) {
        ElectionResponse response = new ElectionResponse();
        response.setId(election.getId());
        response.setTitle(election.getTitle());
        response.setStartDateTime(election.getStartDateTime());
        response.setEndDateTime(election.getEndDateTime());
        response.setStatus(ElectionStatus.getStatus(election));
        response.setPositionIds(election.getPositionIds());
        return response;
    }

    public static Position map(PositionRequest positionRequest) {
        Position position = new Position();
        position.setTitle(positionRequest.getTitle());
        position.setElectionId(positionRequest.getElectionId());
        return position;
    }

    public static PositionResponse map(Position position) {
        PositionResponse response = new PositionResponse();
        response.setId(position.getId());
        response.setTitle(position.getTitle());
        response.setElectionId(position.getElectionId());
        return response;
    }
}
