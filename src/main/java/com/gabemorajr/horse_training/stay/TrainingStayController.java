package com.gabemorajr.horse_training.stay;

import com.gabemorajr.horse_training.stay.dto.CreateStayRequest;
import com.gabemorajr.horse_training.stay.dto.EndStayRequest;
import com.gabemorajr.horse_training.stay.dto.StayResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stays")
public class TrainingStayController {

    private final TrainingStayService stayService;

    public TrainingStayController(TrainingStayService stayService) {
        this.stayService = stayService;
    }

    @GetMapping("/active")
    public List<StayResponse> listActiveStays() {
        return stayService.listActiveStays();
    }

    @GetMapping("/{id}")
    public StayResponse getStay(@PathVariable Long id) {
        return stayService.getStay(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created instead of 200 OK
    public StayResponse createStay(@Valid @RequestBody CreateStayRequest request) {
        return stayService.createStay(request);
    }

    @PostMapping("/{id}/end")
    public StayResponse endStay(@PathVariable Long id, @Valid @RequestBody EndStayRequest request) {
        return stayService.endStay(id, request);
    }
}
