package com.gabemorajr.horse_training.session;

import jakarta.persistence.*;
import com.gabemorajr.horse_training.stay.TrainingStay;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TrainingStay stay;     // maps to stay_id

    private LocalDate sessionDate;
    private Integer durationMinutes;   // Integer: optional, can be null
    private String focus;
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    protected TrainingSession() {
    }

    public TrainingSession(TrainingStay stay, LocalDate sessionDate, Integer durationMinutes, String focus, String notes) {
        this.stay = stay;
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.focus = focus;
        this.notes = notes;
    }

    public void update(LocalDate sessionDate, Integer durationMinutes, String focus, String notes) {
        this.sessionDate = sessionDate;
        this.durationMinutes = durationMinutes;
        this.focus = focus;
        this.notes = notes;
    }

    // getters
    public Long getId() { return id; }
    public TrainingStay getStay() { return stay; }
    public LocalDate getSessionDate() { return sessionDate; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public String getFocus() { return focus; }
    public String getNotes() { return notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}