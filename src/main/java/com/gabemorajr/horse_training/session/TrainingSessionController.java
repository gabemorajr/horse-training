package com.gabemorajr.horse_training.session;

import com.gabemorajr.horse_training.session.dto.SessionRequest;
import com.gabemorajr.horse_training.session.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TrainingSessionController {

    private final TrainingSessionService sessionService;

    public TrainingSessionController(TrainingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/stays/{stayId}/sessions")
    public List<SessionResponse> listSessions(@PathVariable Long stayId) {
        return sessionService.listSessions(stayId);
    }

    @GetMapping("/sessions/{id}")
    public SessionResponse getSession(@PathVariable Long id) {
        return sessionService.getSession(id);
    }

    @PostMapping("/stays/{stayId}/sessions")
    @ResponseStatus(HttpStatus.CREATED) // 201 Created instead of 200 OK
    public SessionResponse createSession(@PathVariable Long stayId, @Valid @RequestBody SessionRequest request) {
        return sessionService.createSession(stayId, request);
    }

    @PutMapping("/sessions/{id}")
    public SessionResponse updateSession(@PathVariable Long id, @Valid @RequestBody SessionRequest request) {
        return sessionService.updateSession(id, request);
    }

    @DeleteMapping("/sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: success, nothing to return
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
    }
}
