package ai.fit.monk.service;

import ai.fit.monk.model.DashboardResponse;
import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.User;
import ai.fit.monk.model.UserDto;
import ai.fit.monk.repository.MonkDailyLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MonkDailyLogRepository repo;

    private final AIInsightService aiInsightService;

    public DashboardResponse getDashboard(User user) {

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(29);

        List<MonkDailyLog> logs = repo.findByUserAndLogDateBetweenOrderByLogDateAsc(user, start, today);

        // ✅ Convert logs
       /* List<MonkDailyLog> logDtos = logs.stream()
                .map(log -> MonkDailyLogDto.builder()
                        .date(log.getDate())
                        .score(log.calculateScore())
                        .calories(log.getCalories())
                        .build())
                .collect(Collectors.toList());*/

        // 🔥 Calculate streak
        int streak = calculateStreak(user);

        // 📊 Average score
        int avgScore = logs.stream()
                .mapToInt(MonkDailyLog::getScore)
                .sum() / (logs.size() == 0 ? 1 : logs.size());

        // 🤖 AI Insights (basic logic)
        List<String> insights =aiInsightService.generateInsights(logs);

                //generateInsights(logs);

        return DashboardResponse.builder()
                .user(UserDto.builder()
                        .name(user.getName())
                        .streak(streak)
                        .score(avgScore)
                        .build())
                .logs(logs)
                .insights(insights)
                .build();
    }

    // 🔥 Streak Logic
    private int calculateStreak(User user) {
        List<MonkDailyLog> logs = repo.findByUserOrderByLogDateDesc(user);

        int streak = 0;
        LocalDate expectedDate = LocalDate.now();

        for (MonkDailyLog log : logs) {
            if (log.getLogDate().equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    // 🤖 Simple AI logic (upgrade later)
    private List<String> generateInsights(List<MonkDailyLog> logs) {

        long missedWorkout = logs.stream()
                .filter(l -> l.getScore() < 50)
                .count();

        return List.of(
                "Consistency improving",
                "Missed " + missedWorkout + " low-score days",
                "Focus on workout streak 🔥"
        );
    }
}