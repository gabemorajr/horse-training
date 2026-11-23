package com.gabemorajr.horse_training.session;

import com.gabemorajr.horse_training.stay.TrainingStay;
import com.gabemorajr.horse_training.stay.TrainingStayRepository;
import com.gabemorajr.horse_training.session.dto.SessionResponse;
import com.gabemorajr.horse_training.session.dto.SessionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;
    private final TrainingStayRepository stayRepository;

    public TrainingSessionService(TrainingSessionRepository sessionRepository,
                               TrainingStayRepository stayRepository) {
        this.sessionRepository = sessionRepository;
        this.stayRepository = stayRepository;
    }

    @Transactional
    public SessionResponse createSession(Long stayId, SessionRequest request) {
        TrainingStay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stay " + stayId + " not found")); // 404

        validateSessionDate(stay, request.sessionDate());

        TrainingSession session = new TrainingSession(
                stay, request.sessionDate(), request.durationMinutes(),
                blankToNull(request.focus()), blankToNull(request.notes()));

        TrainingSession saved = sessionRepository.save(session);

        return SessionResponse.from(saved);
    }

    private void validateSessionDate(TrainingStay stay, LocalDate sessionDate) {
        if (sessionDate.isBefore(stay.getArrivalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, // 400
                    "Session date cannot be before the horse arrived on " + stay.getArrivalDate() + ".");
        }

        if (!stay.isActive() && sessionDate.isAfter(stay.getDepartureDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, // 400
                    "Session date cannot be after the horse left on " + stay.getDepartureDate() + ".");
        }
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> listSessions(Long stayId) {
        // ensure stay exists, throw 404 if dne
        if (!stayRepository.existsById(stayId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stay " + stayId + " not found");
        }

        return sessionRepository.findByStayIdOrderBySessionDateDescIdDesc(stayId)
                .stream()
                .map(SessionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionResponse getSession(Long id) {
        return SessionResponse.from(findSession(id));
    }

    @Transactional
    public SessionResponse updateSession(Long sessionId, SessionRequest request) {
        TrainingSession session = findSession(sessionId);

        TrainingStay stay = session.getStay();

        validateSessionDate(stay, request.sessionDate());

        session.update(request.sessionDate(), request.durationMinutes(),
                blankToNull(request.focus()), blankToNull(request.notes()));

        return SessionResponse.from(session);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        TrainingSession session = findSession(sessionId);
        sessionRepository.delete(session);
    }

    private TrainingSession findSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session " + id + " not found"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

}