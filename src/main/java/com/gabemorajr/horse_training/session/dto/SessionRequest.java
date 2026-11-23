package com.gabemorajr.horse_training.session.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record SessionRequest(
        @NotNull @PastOrPresent LocalDate sessionDate,
        @Positive @Max(480) Integer durationMinutes,
        @Size(max = 255) String focus,
        @Size(max = 5000) String notes
) {
}