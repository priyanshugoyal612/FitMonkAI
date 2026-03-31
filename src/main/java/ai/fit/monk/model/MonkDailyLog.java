package ai.fit.monk.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "monk_daily_log",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "log_date"})
        })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MonkDailyLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "calories_intake", nullable = false)
    private int caloriesIntake;


    @Column(name = "daily_steps", nullable = false)
    private int dailySteps;

    @Column(name = "workout_done", nullable = false)
    private boolean workoutDone;

    @Column(name = "focus_hours", nullable = false)
    private boolean focusHours;

    @Column(name = "no_dopamine", nullable = false)
    private boolean noDopamine; // no reels/porn/alcohol

    @Column(name = "notes", length = 10000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "streak", nullable = false)
    private int streak;

    @Column(name = "score", nullable = false)
    private int score;


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


}
