package com.gabemorajr.horse_training.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findByStayIdOrderBySessionDateDescIdDesc(Long stayId);
}
