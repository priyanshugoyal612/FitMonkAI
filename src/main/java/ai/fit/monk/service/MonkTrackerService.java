package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.utility.FitMonkAIHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Getter
@Setter
public class MonkTrackerService {


    private final MonkDailyLogRepository monkDailyLogRepository;
    private final FitMonkAIHelper fitMonkAIHelper;



    public MonkDailyLog saveLog(MonkDailyLog log) {
        LocalDate logDate = log.getLogDate() != null ? log.getLogDate() : LocalDate.now();
        log.setLogDate(logDate);
        log.setId(null);
        int score = fitMonkAIHelper.calculateScore(log);
        log.setScore(score);
        int streak = fitMonkAIHelper.calculateStreak(log.getUserId(), logDate);
        log.setStreak(streak);

        return monkDailyLogRepository.save(log);
    }

    public MonkDailyLog updateLog(MonkDailyLog log) {
        return monkDailyLogRepository.save(log);
    }

    public MonkDailyLog getLog(Long logId) {
        return monkDailyLogRepository.findById(logId).orElse(null);
    }

}
