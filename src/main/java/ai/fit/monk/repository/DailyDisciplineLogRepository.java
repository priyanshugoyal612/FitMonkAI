package ai.fit.monk.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ai.fit.monk.domain.DailyDisciplineLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyDisciplineLogRepository extends JpaRepository<DailyDisciplineLog, Long> {

    Optional<DailyDisciplineLog> findByUserIdAndLogDate(String userId, LocalDate logDate);

    List<DailyDisciplineLog> findByUserIdAndLogDateBetweenOrderByLogDateDesc(String userId, LocalDate startDate,
            LocalDate endDate);
}

