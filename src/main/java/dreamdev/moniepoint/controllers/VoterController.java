package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.dtos.requests.VoteRequest;
import dreamdev.moniepoint.dtos.requests.VoterRequest;
import dreamdev.moniepoint.services.VoterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.exceptions.ElectionAppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VoterController {
    @Autowired
    private VoterService voterService;

    @PostMapping("/voter")
    public ResponseEntity<?> registerVoter(@RequestBody VoterRequest voterRequest) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, voterService.registerVoter(voterRequest)), HttpStatus.CREATED);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/voters")
    public ResponseEntity<?> getVoters(){
        try {
            return new ResponseEntity<>(new ApiResponse(true, voterService.getVoters()), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/voter/{id}")
    public ResponseEntity<?> getVoter(@PathVariable("id") String id){
        try {
            return new ResponseEntity<>(new ApiResponse(true, voterService.getVoter(id)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/vote")
    public ResponseEntity<?> vote(@RequestBody VoteRequest voteRequest) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, voterService.voteCandidate(voteRequest)), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
