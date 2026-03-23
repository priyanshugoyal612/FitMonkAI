package ai.fit.monk.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "daily_discipline_log", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_date", columnNames = { "user_id", "log_date" })
})
public class DailyDisciplineLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 80)
    private String userId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "workout_done", nullable = false)
    private boolean workoutDone;

    @Column(name = "diet_score", nullable = false)
    private int dietScore;

    @Column(name = "steps", nullable = false)
    private int steps;

    @Column(name = "focus_hours", nullable = false)
    private double focusHours;

    @Column(name = "dopamine_control_score", nullable = false)
    private int dopamineControlScore;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public boolean isWorkoutDone() {
        return workoutDone;
    }

    public void setWorkoutDone(boolean workoutDone) {
        this.workoutDone = workoutDone;
    }

    public int getDietScore() {
        return dietScore;
    }

    public void setDietScore(int dietScore) {
        this.dietScore = dietScore;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getFocusHours() {
        return focusHours;
    }

    public void setFocusHours(double focusHours) {
        this.focusHours = focusHours;
    }

    public int getDopamineControlScore() {
        return dopamineControlScore;
    }

    public void setDopamineControlScore(int dopamineControlScore) {
        this.dopamineControlScore = dopamineControlScore;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

