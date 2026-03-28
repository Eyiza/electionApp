package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.dtos.requests.CandidateRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.exceptions.ElectionAppException;
import dreamdev.moniepoint.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/candidates")
    public ResponseEntity<?> getCandidates(){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getAllCandidates()), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<?> getCandidate(@PathVariable String id){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getCandidate(id)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/candidate/{position}/{firstName}/{lastName}")
    public ResponseEntity<?> getCandidate(@PathVariable CandidateRequest candidateRequest){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getCandidate(candidateRequest)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/candidate/{position}/{id}")
    public ResponseEntity<?> voteCandidate(@PathVariable String id, String position){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.voteCandidate(id, position)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/candidate/{position}/{firstName}/{lastName}")
    public ResponseEntity<?> voteCandidate(@PathVariable CandidateRequest candidateRequest){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.voteCandidate(candidateRequest)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
