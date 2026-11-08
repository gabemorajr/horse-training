package com.gabemorajr.horse_training.horse;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorseRepository extends JpaRepository<Horse, Long> {

    List<Horse> findByArchivedAtIsNullOrderByNameAsc();
}
