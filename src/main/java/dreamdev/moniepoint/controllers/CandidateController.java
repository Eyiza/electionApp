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

    @GetMapping("/election/{electionId}/candidates")
    public ResponseEntity<?> getCandidates( @PathVariable("electionId") String electionId){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getAllCandidates(electionId)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/candidate/{id}")
    public ResponseEntity<?> getCandidate(@PathVariable("id") String id){
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getCandidate(id)), HttpStatus.OK);
        } catch (ElectionAppException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/election/{electionId}/candidates/search")
    public ResponseEntity<?> searchCandidates(
            @PathVariable("electionId") String electionId,
            @RequestParam(value = "firstName",required = false, defaultValue = "") String firstName,
            @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName,
            @RequestParam(value = "position", required = false, defaultValue = "") String position) {
        try {
            return new ResponseEntity<>(
                    new ApiResponse(true, candidateService.searchCandidates(electionId, firstName, lastName)),
                    HttpStatus.OK
            );
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/results")
    public ResponseEntity<?> getResults() {
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getResults()), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/results/{position}")
    public ResponseEntity<?> getResultsByPosition(@PathVariable("position") String position) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, candidateService.getResultsByPosition(position)), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

