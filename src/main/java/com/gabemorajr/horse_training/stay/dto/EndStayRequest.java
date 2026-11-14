package com.gabemorajr.horse_training.stay.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EndStayRequest(
        @NotNull @PastOrPresent LocalDate departureDate
) {
}