package ai.fit.monk.service;

import lombok.RequiredArgsConstructor;

import ai.fit.monk.rest.dto.DailyDisciplineLogResponse;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FitMonkAIService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String getResponseFromFitMonk(String chat, String conversationId) {

        List<Document> docs = vectorStore.similaritySearch(chat);
        String context = docs.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        return this.chatClient.prompt().system("You are FitMonk, a helpful and precise assistant for fitness related questions. Use the following pieces of context to answer the question at the end. .\n\nContext:\n" + context)
                .user(chat)
                .advisors(
                        advisorSpec -> advisorSpec.param("conversationId", conversationId))
                .call()
                .content();
    }

    public String generateDisciplineFeedback(DailyDisciplineLogResponse log,
            List<DailyDisciplineLogResponse> recentLogs,
            String conversationId) {

        String retrievalQuery = "monk mode discipline workout diet steps focus dopamine control";
        List<Document> docs = vectorStore.similaritySearch(retrievalQuery);
        String context = docs.stream().map(Document::getText).reduce("", (a, b) -> a + "\n" + b);

        String recentLogsSummary = recentLogs.stream()
                .map(item -> item.logDate() + " workout=" + item.workoutDone()
                        + ", diet=" + item.dietScore()
                        + ", steps=" + item.steps()
                        + ", focus=" + item.focusHours()
                        + ", dopamine=" + item.dopamineControlScore())
                .reduce("", (a, b) -> a + "\n" + b);

        String prompt = "Give practical daily feedback on this monk-mode check-in. "
                + "Include short sections: Workout, Diet, Steps, Focus, Dopamine Control, Next 1 Action.\n"
                + "Current Log:\n"
                + "date=" + log.logDate()
                + ", workout=" + log.workoutDone()
                + ", dietScore=" + log.dietScore()
                + ", steps=" + log.steps()
                + ", focusHours=" + log.focusHours()
                + ", dopamineControlScore=" + log.dopamineControlScore()
                + ", notes=" + log.notes()
                + "\n\nRecent Logs:\n" + recentLogsSummary;

        return this.chatClient.prompt()
                .system("You are FitMonk, a disciplined coach. Use this context when useful:\n" + context)
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                .call()
                .content();
    }


}

