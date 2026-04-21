package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.dtos.requests.ElectionRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.exceptions.ElectionAppException;
import dreamdev.moniepoint.services.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    @PostMapping("/election")
    public ResponseEntity<?> createElection(@RequestBody ElectionRequest electionRequest) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, electionService.createElection(electionRequest)), HttpStatus.CREATED);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/election/{id}")
    public ResponseEntity<?> getElection(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, electionService.getElection(id)), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        try {
            return new ResponseEntity<>(new ApiResponse(true, electionService.getAllElections()), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
