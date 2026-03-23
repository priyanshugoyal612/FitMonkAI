package ai.fit.monk.service;

import java.time.LocalDate;
import java.util.List;

import ai.fit.monk.domain.DailyDisciplineLog;
import ai.fit.monk.repository.DailyDisciplineLogRepository;
import ai.fit.monk.rest.dto.DailyDisciplineLogRequest;
import ai.fit.monk.rest.dto.DailyDisciplineLogResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DisciplineTrackerService {

    private final DailyDisciplineLogRepository dailyDisciplineLogRepository;

    public DisciplineTrackerService(DailyDisciplineLogRepository dailyDisciplineLogRepository) {
        this.dailyDisciplineLogRepository = dailyDisciplineLogRepository;
    }

    public DailyDisciplineLogResponse upsertDailyLog(DailyDisciplineLogRequest request) {
        validateRequest(request);

        DailyDisciplineLog log = dailyDisciplineLogRepository
                .findByUserIdAndLogDate(request.userId(), request.logDate())
                .orElseGet(DailyDisciplineLog::new);

        log.setUserId(request.userId().trim());
        log.setLogDate(request.logDate());
        log.setWorkoutDone(request.workoutDone());
        log.setDietScore(request.dietScore());
        log.setSteps(request.steps());
        log.setFocusHours(request.focusHours());
        log.setDopamineControlScore(request.dopamineControlScore());
        log.setNotes(request.notes());

        return toResponse(dailyDisciplineLogRepository.save(log));
    }

    public DailyDisciplineLogResponse getDailyLog(String userId, LocalDate logDate) {
        return dailyDisciplineLogRepository
                .findByUserIdAndLogDate(userId, logDate)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("No daily log found for user and date"));
    }

    public List<DailyDisciplineLogResponse> getLogs(String userId, LocalDate startDate, LocalDate endDate) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("userId is required");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }

        return dailyDisciplineLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDateDesc(userId.trim(), startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DailyDisciplineLogResponse> getRecentLogs(String userId, LocalDate logDate, int days) {
        LocalDate fromDate = logDate.minusDays(Math.max(days - 1, 0));
        return getLogs(userId, fromDate, logDate);
    }

    private void validateRequest(DailyDisciplineLogRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (!StringUtils.hasText(request.userId())) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.logDate() == null) {
            throw new IllegalArgumentException("logDate is required");
        }
        if (request.dietScore() < 0 || request.dietScore() > 10) {
            throw new IllegalArgumentException("dietScore must be between 0 and 10");
        }
        if (request.steps() < 0) {
            throw new IllegalArgumentException("steps cannot be negative");
        }
        if (request.focusHours() < 0) {
            throw new IllegalArgumentException("focusHours cannot be negative");
        }
        if (request.dopamineControlScore() < 0 || request.dopamineControlScore() > 10) {
            throw new IllegalArgumentException("dopamineControlScore must be between 0 and 10");
        }
    }

    private DailyDisciplineLogResponse toResponse(DailyDisciplineLog log) {
        return new DailyDisciplineLogResponse(
                log.getId(),
                log.getUserId(),
                log.getLogDate(),
                log.isWorkoutDone(),
                log.getDietScore(),
                log.getSteps(),
                log.getFocusHours(),
                log.getDopamineControlScore(),
                log.getNotes());
    }
}

