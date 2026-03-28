package dreamdev.moniepoint.controllers;

import dreamdev.moniepoint.data.models.Candidate;
import dreamdev.moniepoint.services.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PostMapping("/candidate")
    public Candidate addCandidate(){
        return null;
    }
}
