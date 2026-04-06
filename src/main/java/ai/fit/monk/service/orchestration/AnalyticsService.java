package ai.fit.monk.service.orchestration;


import ai.fit.monk.model.User;
import ai.fit.monk.service.FitMonkAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final FitMonkAIService fitMonkAIService;

    public String handle(User user) {
        return fitMonkAIService.getWeeklyReport(user);
    }
}