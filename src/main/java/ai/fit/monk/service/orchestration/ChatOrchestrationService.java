package ai.fit.monk.service.orchestration;

import ai.fit.monk.enums.IntentType;
import ai.fit.monk.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ai.fit.monk.enums.QueryType;

@Service
@RequiredArgsConstructor
public class ChatOrchestrationService {

    private final IntentClassifier intentClassifier;
    private final RagPipelineService ragPipelineService;
    private final LogService logService;
    private final AnalyticsService analyticsService;
    private final CoachService coachService;
    private final QueryClassifier queryClassifier; // 🔥 ADD THIS

    public String handle(String message, User user, String conversationId) {

        IntentType intent = intentClassifier.classify(message);

        switch (intent) {

            case LOGGING:
                return logService.handle(message, user, conversationId);

            case ANALYSIS:
                return analyticsService.handle(user);

            case QUERY:
                return handleQuery(message, user, conversationId);

            case COACHING:
            case CASUAL:
            default:
                return coachService.handle(message, user, conversationId);
        }
    }

    // =====================================
    // 🔥 QUERY HANDLER (IMPORTANT)
    // =====================================
    private String handleQuery(String message, User user, String conversationId) {

        QueryType queryType = queryClassifier.classify(message);

        // 🔹 GENERAL → no RAG
        if (queryType == QueryType.GENERAL) {
            return coachService.handle(message, user, conversationId);
        }

        // 🔹 CONTEXTUAL → use RAG
        return ragPipelineService.handle(message, user, conversationId);
    }
}