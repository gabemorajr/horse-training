package com.gabemorajr.horse_training.stay;

import com.gabemorajr.horse_training.stay.dto.EndStayRequest;
import com.gabemorajr.horse_training.stay.dto.StayResponse;
import com.gabemorajr.horse_training.stay.dto.CreateStayRequest;
import com.gabemorajr.horse_training.horse.Horse;
import com.gabemorajr.horse_training.client.Client;
import com.gabemorajr.horse_training.client.ClientRepository;
import com.gabemorajr.horse_training.horse.HorseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TrainingStayService {

    private final TrainingStayRepository stayRepository;
    private final HorseRepository horseRepository;
    private final ClientRepository clientRepository;

    public TrainingStayService(TrainingStayRepository stayRepository,
                               HorseRepository horseRepository,
                               ClientRepository clientRepository) {
        this.stayRepository = stayRepository;
        this.horseRepository = horseRepository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    public StayResponse createStay(CreateStayRequest request) {
        Horse horse = horseRepository.findById(request.horseId())
                .filter(h -> !h.isArchived())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Horse " + request.horseId() + " not found"));

        Client client = clientRepository.findById(request.clientId())
                .filter(c -> !c.isArchived())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client " + request.clientId() + " not found"));

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
        // rules: already ended? departure before arrival?
        if (!stay.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This stay already ended on " + stay.getDepartureDate() + ".");
        }

        if (request.departureDate().isBefore(stay.getArrivalDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Departure date can't be before the arrival date (" + stay.getArrivalDate() + ").");
        }

        stay.end(request.departureDate());
        return StayResponse.from(stay);
    }

    private TrainingStay findStay(Long id) {
        return stayRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stay " + id + " not found"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

}