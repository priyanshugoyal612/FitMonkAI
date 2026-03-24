package ai.fit.monk.model;

import java.time.LocalDate;
import java.util.List;

public record WeeklySummary(
        String userId,
        LocalDate startDate,
        LocalDate endDate,
        int totalDaysLogged,
        int totalCalories,
        int totalSteps,
        int workoutDays,
        int focusDays,
        int noDopamineDays,
        double averageScore,
        int currentStreak,
        List<String> notes,
        List<MonkDailyLog> logs,
        String summaryText

) {}
