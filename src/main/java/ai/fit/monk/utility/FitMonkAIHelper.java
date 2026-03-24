package ai.fit.monk.utility;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.repository.MonkDailyLogRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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


        Optional<MonkDailyLog> lastLogOpt =
                monkDailyLogRepository.findTopByUserIdOrderByLogDateDesc(userId);

        // No logs → first streak
        if (lastLogOpt.isEmpty()) return 1;

        MonkDailyLog lastLog = lastLogOpt.get();
        LocalDate lastDate = lastLog.getLogDate();

        // Same day → no change
        if (lastDate.equals(logDate)) {
            return lastLog.getStreak();
        }

        // Check continuity
        if (lastDate.plusDays(1).equals(logDate)) {
            return lastLog.getStreak() + 1;
        }

        // Gap → reset
        return 1;
    }

}
