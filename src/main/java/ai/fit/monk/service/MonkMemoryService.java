package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonkMemoryService {

    private final VectorStore vectorStore;

    public void storeLog(MonkDailyLog log) {

        if (!shouldStore(log)) return;

        String content = buildContent(log);

        Map<String, Object> metadata = Map.of(
                "type", "memory",
                "subType", log.getScore() < 60 ? "failure" : "success",
                "userId", log.getUser().getUserId(),
                "score", log.getScore(),
                "date", log.getLogDate().toString()
        );

        vectorStore.add(List.of(new Document(content, metadata)));
    }

    private boolean shouldStore(MonkDailyLog log) {
        return log.getScore() < 50
                || log.getScore() > 85
                || !log.isWorkoutDone()
                || !log.isFocusHours();
    }

    private String buildContent(MonkDailyLog log) {
        return String.format(
                "Score: %d | Workout: %s | Focus: %s | Insight: %s",
                log.getScore(),
                log.isWorkoutDone(),
                log.isFocusHours(),
                generateInsight(log)
        );
    }

    private String generateInsight(MonkDailyLog log) {

        if (log.getScore() < 40) return "Very low discipline day";
        if (!log.isWorkoutDone()) return "Workout missed";
        if (!log.isFocusHours()) return "Low focus";
        if (log.getScore() > 85) return "High discipline day";

        return "Normal day";
    }

    public List<Document> findSimilarFailures(String userId) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("discipline failure patterns")
                        .filterExpression(
                                "type == 'memory' AND subType == 'failure' AND userId == '" + userId + "'"
                        )
                        .topK(5)
                        .build()
        );
    }
}