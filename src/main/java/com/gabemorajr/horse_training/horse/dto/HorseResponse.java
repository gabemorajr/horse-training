package com.gabemorajr.horse_training.horse.dto;

import com.gabemorajr.horse_training.horse.Horse;
import com.gabemorajr.horse_training.horse.HorseSex;

public record HorseResponse(
        Long id,
        String name,
        String breed,
        HorseSex sex,
        String color,
        Integer birthYear,
        String notes
) {
    public static HorseResponse from(Horse horse) {
        return new HorseResponse(
                horse.getId(),
                horse.getName(),
                horse.getBreed(),
                horse.getSex(),
                horse.getColor(),
                horse.getBirthYear(),
                horse.getNotes()
        );
    }
}
