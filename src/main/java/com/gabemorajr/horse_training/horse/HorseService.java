package com.gabemorajr.horse_training.horse;

import com.gabemorajr.horse_training.horse.dto.HorseRequest;
import com.gabemorajr.horse_training.horse.dto.HorseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HorseService {

    private final HorseRepository horseRepository;

    public HorseService(HorseRepository horseRepository) {
        this.horseRepository = horseRepository;
    }

    @Transactional(readOnly = true)
    public List<HorseResponse> listActiveHorses() {
        return horseRepository.findByArchivedAtIsNullOrderByNameAsc()
                .stream()
                .map(HorseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public HorseResponse getHorse(Long id) {
        return HorseResponse.from(findHorse(id));
    }

    @Transactional
    public HorseResponse createHorse(HorseRequest request) {
        Horse horse = new Horse(
                request.name().trim(),
                blankToNull(request.breed()),
                request.sex(),
                blankToNull(request.color()),
                request.birthYear(),
                blankToNull(request.notes())
        );
        Horse saved = horseRepository.save(horse); // Hibernate runs the INSERT
        return HorseResponse.from(saved);
    }

    @Transactional
    public HorseResponse updateHorse(Long id, HorseRequest request) {
        Horse horse = findHorse(id);

        horse.update(
                request.name().trim(),
                blankToNull(request.breed()),
                request.sex(),
                blankToNull(request.color()),
                request.birthYear(),
                blankToNull(request.notes())
        );
        // no need to call horseRepository.save() since dirty checking writes the update automatically
        return HorseResponse.from(horse);
    }

    @Transactional
    public void archiveHorse(Long id) {
        Horse horse = findHorse(id);
        horse.archive();
    }

    private Horse findHorse(Long id) {
        return horseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Horse " + id + " not found"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
