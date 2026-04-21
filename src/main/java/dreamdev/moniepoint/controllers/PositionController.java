package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.dtos.requests.PositionRequest;
import dreamdev.moniepoint.dtos.responses.ApiResponse;
import dreamdev.moniepoint.exceptions.ElectionAppException;
import dreamdev.moniepoint.services.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PositionController {

    @Autowired
    private PositionService positionService;

    @PostMapping("/position")
    public ResponseEntity<?> createPosition(@RequestBody PositionRequest positionRequest) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, positionService.createPosition(positionRequest)), HttpStatus.CREATED);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/position/{id}")
    public ResponseEntity<?> getPosition(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, positionService.getPosition(id)), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/election/{electionId}/positions")
    public ResponseEntity<?> getPositionsByElection(@PathVariable("electionId") String electionId) {
        try {
            return new ResponseEntity<>(new ApiResponse(true, positionService.getPositionsByElection(electionId)), HttpStatus.OK);
        } catch (ElectionAppException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}