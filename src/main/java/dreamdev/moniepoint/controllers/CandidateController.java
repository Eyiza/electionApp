package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.exceptions.ElectionAppException;
import dreamdev.moniepoint.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PostMapping("/candidate")
    public ResponseEntity<?> addCandidate(@RequestBody CandidateRequest candidateRequest){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.createCandidate(candidateRequest)), HttpStatus.CREATED);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
