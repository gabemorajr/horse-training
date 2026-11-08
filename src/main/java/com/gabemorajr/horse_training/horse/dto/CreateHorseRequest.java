package com.gabemorajr.horse_training.horse.dto;

import com.gabemorajr.horse_training.horse.HorseSex;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHorseRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 100) String breed,
        HorseSex sex,
        @Size(max = 50) String color,
        @Min(1980) @Max(2100) Integer birthYear,
        String notes
) {
}
