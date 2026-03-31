package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.WeeklySummary;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.utility.FitMonkAIHelper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Getter
@Setter
public class MonkTrackerService {


    private final MonkDailyLogRepository monkDailyLogRepository;
    private final FitMonkAIHelper fitMonkAIHelper;
    private MonkMemoryService memoryService;




    @Transactional
    public MonkDailyLog saveLog(MonkDailyLog log) {
        LocalDate logDate = log.getLogDate() != null ? log.getLogDate() : LocalDate.now();
        log.setLogDate(logDate);
        log.setId(null);
        int score = fitMonkAIHelper.calculateScore(log);
        log.setScore(score);
        int streak = fitMonkAIHelper.calculateStreak(log.getUser(), logDate);
        log.setStreak(streak);

        MonkDailyLog saved = monkDailyLogRepository.save(log);

        memoryService.storeLog(saved); // 🔥 NEW

        return saved;
    }

    @Transactional
    public MonkDailyLog updateLog(MonkDailyLog log) {
        MonkDailyLog dbMonkLog=monkDailyLogRepository.findByUserAndLogDate(log.getUser(), log.getLogDate());
        log.setId(dbMonkLog.getId());
        return monkDailyLogRepository.save(log);
    }

    public MonkDailyLog getLog(Long logId) {
        return monkDailyLogRepository.findById(logId).orElse(null);
    }

}
