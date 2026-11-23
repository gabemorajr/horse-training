package com.gabemorajr.horse_training.session.dto;

import com.gabemorajr.horse_training.session.TrainingSession;

import java.time.LocalDate;

public record SessionResponse(
        Long id,
        Long stayId,
        LocalDate sessionDate,
        Integer durationMinutes,
        String focus,
        String notes
) {
    public static SessionResponse from(TrainingSession session) {
        return new SessionResponse(
                session.getId(),
                session.getStay().getId(),
                session.getSessionDate(),
                session.getDurationMinutes(),
                session.getFocus(),
                session.getNotes()
        );
    }
}
