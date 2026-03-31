package ai.fit.monk.repository;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MonkDailyLogRepository extends JpaRepository<MonkDailyLog, Long> {


    List<MonkDailyLog> findByUser(User user);

    Optional<MonkDailyLog> findTopByUserOrderByLogDateDesc(User user);

    Optional<MonkDailyLog> findFirstByUserAndLogDateOrderByIdAsc(User user, LocalDate logDate);

    List<MonkDailyLog> findByUserOrderByLogDateDesc(User user);

    List<MonkDailyLog> findByUserAndLogDateBetweenOrderByLogDateAsc(
            User user,
            LocalDate startDate,
            LocalDate endDate
    );

    List<MonkDailyLog> findTop7ByUserOrderByLogDateDesc(User user);

    MonkDailyLog findByUserAndLogDate(User user, LocalDate logDate);
}
