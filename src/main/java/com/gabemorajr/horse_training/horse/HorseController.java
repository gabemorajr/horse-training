package com.gabemorajr.horse_training.horse;

import com.gabemorajr.horse_training.horse.dto.CreateHorseRequest;
import com.gabemorajr.horse_training.horse.dto.HorseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horses")
public class HorseController {

    private final HorseService horseService;

    public HorseController(HorseService horseService) {
        this.horseService = horseService;
    }

    @GetMapping
    public List<HorseResponse> listHorses() {
        return horseService.listActiveHorses();
    }

    @GetMapping("/{id}")
    public HorseResponse getHorse(@PathVariable Long id) {
        return horseService.getHorse(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created instead of 200 OK
    public HorseResponse createHorse(@Valid @RequestBody CreateHorseRequest request) {
        return horseService.createHorse(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204: success, nothing to return
    public void archiveHorse(@PathVariable Long id) {
        horseService.archiveHorse(id);
    }
}
