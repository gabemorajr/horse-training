package com.gabemorajr.horse_training.stay.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateStayRequest(
        @NotNull Long horseId,
        @NotNull Long clientId,
        @NotNull LocalDate arrivalDate,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal monthlyRate,
        @NotNull Boolean feedIncluded,
        @Size(max = 5000) String goals

        ) {
}