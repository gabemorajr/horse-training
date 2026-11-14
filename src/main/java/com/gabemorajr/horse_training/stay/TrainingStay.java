package com.gabemorajr.horse_training.stay;

import com.gabemorajr.horse_training.client.Client;
import com.gabemorajr.horse_training.horse.Horse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "training_stays")
public class TrainingStay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Horse horse;      // maps to the horse_id column

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;    // maps to the client_id column

    private LocalDate arrivalDate;
    private LocalDate departureDate;   // null = still in training
    private String goals;
    private BigDecimal monthlyRate;    // BigDecimal for money, never double
    private boolean feedIncluded;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    protected TrainingStay() {
    }

    public TrainingStay(Horse horse, Client client, LocalDate arrivalDate,
                        BigDecimal monthlyRate, boolean feedIncluded, String goals) {
        this.horse = horse;
        this.client = client;
        this.arrivalDate = arrivalDate;
        this.monthlyRate = monthlyRate;
        this.feedIncluded = feedIncluded;
        this.goals = goals;
    }

    public boolean isActive() {
        return departureDate == null;
    }

    public void end(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Long getId() { return id; }
    public Horse getHorse() { return horse; }
    public Client getClient() { return client; }
    public LocalDate getArrivalDate() { return arrivalDate; }
    public LocalDate getDepartureDate() { return departureDate; }
    public String getGoals() { return goals; }
    public BigDecimal getMonthlyRate() { return monthlyRate; }
    public boolean getFeedIncluded() { return feedIncluded; }
}