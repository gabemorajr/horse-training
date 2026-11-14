package com.gabemorajr.horse_training.stay.dto;

import com.gabemorajr.horse_training.stay.TrainingStay;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StayResponse(
        Long id,
        Long horseId,
        String horseName,
        Long clientId,
        String clientName,
        LocalDate arrivalDate,
        LocalDate departureDate,
        String goals,
        BigDecimal monthlyRate,
        boolean feedIncluded
) {
    public static StayResponse from(TrainingStay stay) {
        return new StayResponse(
                stay.getId(),
                stay.getHorse().getId(),
                stay.getHorse().getName(),
                stay.getClient().getId(),
                stay.getClient().getFirstName() + " " + stay.getClient().getLastName(),
                stay.getArrivalDate(),
                stay.getDepartureDate(),
                stay.getGoals(),
                stay.getMonthlyRate(),
                stay.getFeedIncluded()
        );
    }
}