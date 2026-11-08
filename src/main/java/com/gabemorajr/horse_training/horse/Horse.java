package com.gabemorajr.horse_training.horse;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "horses")
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String breed;

    @Enumerated(EnumType.STRING)
    private HorseSex sex;

    private String color;

    @Column(name = "birth_year")
    private Integer birthYear;

    private String notes;

    @Column(name = "archived_at")
    private OffsetDateTime archivedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected Horse() {
    }

    public Horse(String name, String breed, HorseSex sex, String color, Integer birthYear, String notes) {
        this.name = name;
        this.breed = breed;
        this.sex = sex;
        this.color = color;
        this.birthYear = birthYear;
        this.notes = notes;
    }

    public void archive() {
        this.archivedAt = OffsetDateTime.now();
    }

    public boolean isArchived() {
        return archivedAt != null;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public HorseSex getSex() { return sex; }
    public String getColor() { return color; }
    public Integer getBirthYear() { return birthYear; }
    public String getNotes() { return notes; }
    public OffsetDateTime getArchivedAt() { return archivedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
