package ai.fit.monk.repository;

import ai.fit.monk.model.MonkDailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MonkDailyLogRepository extends JpaRepository<MonkDailyLog, Long> {


    List<MonkDailyLog> findByUserId(String userId);

    Optional<MonkDailyLog> findTopByUserIdOrderByLogDateDesc(String userId);

    Optional<MonkDailyLog> findFirstByUserIdAndLogDateOrderByIdAsc(String userId, LocalDate logDate);

    List<MonkDailyLog> findByUserIdOrderByLogDateDesc(String userId);

    List<MonkDailyLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(
            String userId,
            LocalDate startDate,
            LocalDate endDate
    );

}
