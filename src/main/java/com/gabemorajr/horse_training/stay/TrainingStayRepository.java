package com.gabemorajr.horse_training.stay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TrainingStayRepository extends JpaRepository<TrainingStay, Long> {

    // Derived query: "does this horse have a stay with no departure date?"
    // HorseId reaches through the relationship: stay.horse.id
    boolean existsByHorseIdAndDepartureDateIsNull(Long horseId);

    // Hand-written query: active stays, with horse and client loaded in ONE query
    @Query("""
            SELECT s FROM TrainingStay s
            JOIN FETCH s.horse
            JOIN FETCH s.client
            WHERE s.departureDate IS NULL
            ORDER BY s.horse.name
            """)
    List<TrainingStay> findActiveWithHorseAndClient();
}