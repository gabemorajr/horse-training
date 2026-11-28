package com.gabemorajr.horse_training.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByStayIdOrderBySessionDateDescIdDesc(Long stayId);

    // "does this stay have any session dated after the new departure date?"
    boolean existsByStayIdAndSessionDateAfter(Long stayId, LocalDate date);

    boolean existsByStayIdAndSessionDateBefore(Long stayId, LocalDate date);

    @Query("SELECT MAX(ts.sessionDate) FROM TrainingSession ts WHERE ts.stay.id = :stayId")
    LocalDate findLatestSessionDate(@Param("stayId") Long stayId);

    @Query("SELECT MIN(ts.sessionDate) FROM TrainingSession ts WHERE ts.stay.id = :stayId")
    LocalDate findEarliestSessionDate(@Param("stayId") Long stayId);
}
