package ai.fit.monk.service;


import ai.fit.monk.model.MonkDailyLog;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class MonkMemoryService {


    private final VectorStore vectorStore;

    public MonkMemoryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeLog(MonkDailyLog log) {

        String content = String.format(
                "User %s had %d calories, workout: %s, focus: %s, reading: %s, score: %d",
                log.getUser().getUserId(),
                log.getCaloriesIntake(),
                log.isWorkoutDone(),
                log.isFocusHours(),
                log.isNoDopamine(),
                log.getScore()
        );

        vectorStore.add(List.of(
                new Document(content, Map.of("userId", log.getUser().getUserId()))
        ));
    }

    public List<Document> findSimilarFailures(String userId) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("failure patterns")
                        .filterExpression("userId == '" + userId + "'")
                        .build()
        );
    }

}
