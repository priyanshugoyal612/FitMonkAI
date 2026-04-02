package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.MonkLogRequest;
import ai.fit.monk.model.MonkLogResponse;
import ai.fit.monk.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonkLogService {

    private final MonkTrackerService monkTrackerService;
    private final ChatClient chatClient;

    @CacheEvict(value = {"dashboard", "weekly"}, key = "#user.id")
    public MonkLogResponse saveLogWithFeedback(MonkLogRequest request, User user) {
        MonkDailyLog savedLog = saveLog(request, user);

        return MonkLogResponse.builder()
                .message("Daily log saved successfully")
                .aiFeedback(generateFeedback(savedLog, user))
                .build();
    }

    public MonkDailyLog saveLog(MonkLogRequest request, User user) {
        MonkDailyLog log = MonkDailyLog.builder()
                .user(user)
                .logDate(LocalDate.now())
                .caloriesIntake(request.getCalories())
                .dailySteps(request.getSteps())
                .workoutDone(request.isWorkout())
                .focusHours(request.isFocus())
                .noDopamine(request.isNoDopamine())
                .notes(request.getNotes())
                .build();

        return monkTrackerService.saveOrUpdateLog(log);
    }

    private List<String> generateFeedback(MonkDailyLog savedLog, User user) {
        String prompt = """
                You are a strict discipline coach.
                Return exactly 3 short lines.
                No markdown, no numbering, no emojis.

                User: %s
                Log Date: %s
                Calories: %d
                Steps: %d
                Workout: %s
                Focus: %s
                Score: %d
                Streak: %d
                Notes: %s
                """.formatted(
                user.getUserId(),
                savedLog.getLogDate(),
                savedLog.getCaloriesIntake(),
                savedLog.getDailySteps(),
                savedLog.isWorkoutDone(),
                savedLog.isFocusHours(),
                savedLog.getScore(),
                savedLog.getStreak(),
                savedLog.getNotes() == null ? "" : savedLog.getNotes()
        );

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                return fallbackFeedback(savedLog);
            }

            List<String> lines = Arrays.stream(response.split("\\r?\\n"))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .limit(3)
                    .collect(Collectors.toList());

            return lines.isEmpty() ? fallbackFeedback(savedLog) : lines;
        } catch (Exception ex) {
            return fallbackFeedback(savedLog);
        }
    }

    private List<String> fallbackFeedback(MonkDailyLog savedLog) {
        return List.of(
                "Score: " + savedLog.getScore(),
                "Current streak: " + savedLog.getStreak() + " days",
                "Keep consistency tomorrow with workout, steps, and focus."
        );
    }
}
