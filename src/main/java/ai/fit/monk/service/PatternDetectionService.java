package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatternDetectionService {

    public String detectPatterns(List<MonkDailyLog> logs) {
        // Implement pattern detection logic here
        // For example, you can analyze the logs to find common themes, trends, or anomalies
        // This is a placeholder implementation and should be replaced with actual logic


        long highCaloriesFailures = logs.stream()
                .filter(log -> log.getCaloriesIntake() > 2500 && log.getScore() < 50)
                .count();

        if (highCaloriesFailures > 2) {
            return "User tends to fail when calories exceed 2200";
        }

        return "Detected patterns based on the provided logs.";
    }

}
