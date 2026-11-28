package com.gabemorajr.horse_training.stay;

import com.gabemorajr.horse_training.session.TrainingSessionRepository;
import com.gabemorajr.horse_training.stay.dto.EndStayRequest;
import com.gabemorajr.horse_training.stay.dto.StayResponse;
import com.gabemorajr.horse_training.stay.dto.CreateStayRequest;
import com.gabemorajr.horse_training.horse.Horse;
import com.gabemorajr.horse_training.client.Client;
import com.gabemorajr.horse_training.client.ClientRepository;
import com.gabemorajr.horse_training.horse.HorseRepository;
import com.gabemorajr.horse_training.stay.dto.UpdateStayRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TrainingStayService {

    private final TrainingStayRepository stayRepository;
    private final HorseRepository horseRepository;
    private final ClientRepository clientRepository;
    private final TrainingSessionRepository sessionRepository;

    public TrainingStayService(TrainingStayRepository stayRepository,
                               HorseRepository horseRepository,
                               ClientRepository clientRepository,
                               TrainingSessionRepository sessionRepository) {
        this.stayRepository = stayRepository;
        this.horseRepository = horseRepository;
        this.clientRepository = clientRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public StayResponse createStay(CreateStayRequest request) {
        Horse horse = findActiveHorse(request.horseId());
        Client client = findActiveClient(request.clientId());

        // The friendly check. The unique index is the guarantee.
        if (stayRepository.existsByHorseIdAndDepartureDateIsNull(horse.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    horse.getName() + " is already in training. End the current stay first.");
        }

        TrainingStay stay = new TrainingStay(
                horse, client, request.arrivalDate(),
                request.monthlyRate(), request.feedIncluded(), blankToNull(request.goals()));

        TrainingStay saved = stayRepository.save(stay);

        return StayResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<StayResponse> listActiveStays() {
        return stayRepository.findActiveWithHorseAndClient()
                .stream()
                .map(StayResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StayResponse getStay(Long id) {
        return StayResponse.from(findStay(id));
    }

    @Transactional
    public StayResponse endStay(Long id, EndStayRequest request) {
        TrainingStay stay = findStay(id);
        // check if stay has already ended
        if (!stay.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This stay already ended on " + stay.getDepartureDate() + ".");
        }

        validateDepartureDate(id, stay.getArrivalDate(), request.departureDate());

        stay.end(request.departureDate());
        return StayResponse.from(stay);
    }

    @Transactional
    public StayResponse updateStay(Long id, UpdateStayRequest request) {
        // find stay
        TrainingStay stay = findStay(id);
        // find new horse
        Horse horse = findActiveHorse(request.horseId());
        // find new client
        Client client = findActiveClient(request.clientId());
        // save data to variables for reuse
        boolean stayIsActive = stay.isActive();
        LocalDate arrivalDate = request.arrivalDate();
        LocalDate departureDate = request.departureDate();
        // check the horse: an active stay can't switch to a horse that's already in another active stay
        // query asks: "does the new horse have an active stay anywhere?"
        if (stayIsActive &&
                !Objects.equals(stay.getHorse().getId(), request.horseId()) &&
                stayRepository.existsByHorseIdAndDepartureDateIsNull(request.horseId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    horse.getName() + " is already in training. End the current stay first.");
        }
        // validate dates
        validateDeparturePresence(stayIsActive, departureDate);
        validateDepartureDate(id, arrivalDate, departureDate);
        validateArrivalDate(id, arrivalDate);
        // update stay
        stay.update(horse, client, arrivalDate, departureDate,
                request.monthlyRate(), request.feedIncluded(), blankToNull(request.goals()));

        return StayResponse.from(stay);
    }

    private void validateDeparturePresence(boolean stayIsActive, LocalDate departureDate) {
        // new departure date must be null for an active stay
        if (stayIsActive && departureDate != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This stay is currently active. To end it, use the end endpoint.");
        }
        // new departure date is required for an ended stay
        if (!stayIsActive && departureDate == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This stay has ended and requires a departure date.");
        }
    }

    private void validateArrivalDate(Long stayId, LocalDate arrivalDate) {
        // validate arrival date: new arrival date can't be after the earliest session
        LocalDate earliestSessionDate = sessionRepository.findEarliestSessionDate(stayId);
        if (earliestSessionDate != null && arrivalDate.isAfter(earliestSessionDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Arrival date can't be after the first training session on " + earliestSessionDate + ".");
        }
    }

    private void validateDepartureDate(Long stayId, LocalDate arrivalDate, LocalDate departureDate) {
        if (departureDate != null) {
            // new departure date must not be before arrival date
            if (departureDate.isBefore(arrivalDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Departure date can't be before the arrival date (" + arrivalDate + ").");
            }
            // new departure date must not be before the latest session
            LocalDate latestSessionDate = sessionRepository.findLatestSessionDate(stayId);
            if (latestSessionDate != null && departureDate.isBefore(latestSessionDate)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Departure date can't be before the latest training session on " + latestSessionDate + ".");
            }
        }
    }

    private TrainingStay findStay(Long id) {
        return stayRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stay " + id + " not found"));
    }

    private Horse findActiveHorse(Long id) {
        return horseRepository.findById(id)
                .filter(h -> !h.isArchived())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Horse " + id + " not found"));
    }

    private Client findActiveClient(Long id) {
        return clientRepository.findById(id)
                .filter(c -> !c.isArchived())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client " + id + " not found"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

}