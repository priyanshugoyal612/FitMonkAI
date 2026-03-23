package ai.fit.monk.rest.dto;

import java.time.LocalDate;

public record DailyDisciplineLogRequest(
        String userId,
        LocalDate logDate,
        boolean workoutDone,
        int dietScore,
        int steps,
        double focusHours,
        int dopamineControlScore,
        String notes) {
}

