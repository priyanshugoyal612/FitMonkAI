package ai.fit.monk.utility;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.repository.MonkDailyLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FitMonkAIHelper {

    private final MonkDailyLogRepository monkDailyLogRepository;

    public FitMonkAIHelper(MonkDailyLogRepository monkDailyLogRepository) {
        this.monkDailyLogRepository = monkDailyLogRepository;
    }



    public int calculateScore(MonkDailyLog log) {
        Assert.notNull(log, "MonkDailyLog is required");

        int score = 0;

        // Calories (target ~1200-1500)
        if (log.getCaloriesIntake() <= 1500) score += 20;

        // Workout
        if (log.isWorkoutDone()) score += 20;

        // Steps
        if (log.getDailySteps() >= 8000) score += 20;

        // Focus
        if (log.isFocusHours() ) score += 20;

        // No dopamine
        if (log.isNoDopamine()) score += 20;
        return score; // max = 100
    }



    public int calculateStreak(String userId, LocalDate logDate) {


        List<MonkDailyLog> logs = monkDailyLogRepository
                .findByUserId(userId);

        if (logs.isEmpty()) return 0;
        LocalDate today = LocalDate.now();

        // ✅ Unique + ignore future dates
        Set<LocalDate> logDates = logs.stream()
                .map(MonkDailyLog::getLogDate)
                .filter(date -> !date.isAfter(today))
                .collect(Collectors.toSet());

        // ✅ Resolve anchor date
        LocalDate anchor = (logDate != null && !logDate.isAfter(today))
                ? logDate
                : logDates.stream().max(LocalDate::compareTo).get();


        int streak = 0;
        LocalDate current = anchor;
// ✅ If anchor not present → fallback to nearest valid past date
        while (!logDates.contains(current) && current.isAfter(LocalDate.MIN)) {
            current = current.minusDays(1);
        }

        // ❗ If still not found → no logs
        if (!logDates.contains(current)) return 0;

        // ✅ Count consecutive days
        while (logDates.contains(current)) {
            streak++;
            current = current.minusDays(1);
        }

        // ✅ Ensure minimum streak = 1
        return Math.max(streak, 1);
    }

}
