package ai.fit.monk.service.orchestration;

import ai.fit.monk.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import ai.fit.monk.enums.QueryType;

@Service
@RequiredArgsConstructor
public class RagPipelineService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final QueryClassifier queryClassifier;

    public String handle(String message, User user , String conversationId) {

        QueryType type = queryClassifier.classify(message);

        if (type == QueryType.GENERAL) {
            return handleKnowledge(message);
        }

        return handleUserContext(message, user, conversationId);
    }

    // =========================
    // 🔹 KNOWLEDGE RAG
    // =========================
    private String handleKnowledge(String message) {

        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .filterExpression("type == 'knowledge'")
                        .topK(3)
                        .build()
        );

        String context = buildContext(docs);

        return chatClient.prompt()
                .system(buildPrompt(context, message))
                .call()
                .content();
    }

    // =========================
    // 🔹 USER RAG
    // =========================
    private String handleUserContext(String message, User user, String conversationId) {

        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .filterExpression(
                                "type == 'memory' AND userId == '" + user.getUserId() + "'"
                        )
                        .topK(3)
                        .build()
        );

        String context = buildContext(docs);

        return chatClient.prompt()
                .system(buildPrompt(context, message))
                .advisors(a -> a.param("conversationId", conversationId))
                .call()
                .content();
    }

    // =========================
    // 🧠 HELPERS
    // =========================
    private String buildContext(List<Document> docs) {

        if (docs == null || docs.isEmpty()) {
            return "No relevant context.";
        }

        return docs.stream()
                .map(Document::getText)
                .filter(t -> t != null && !t.isBlank())
                .collect(Collectors.joining("\n"));
    }

    private String buildPrompt(String context, String message) {

        return """
            You are a strict Monk Mode Coach.

            RULES:
            - Be direct
            - No fluff
            - Actionable only

            %s

            USER MESSAGE:
            %s
            """.formatted(
                context.contains("No")
                        ? "Answer using general knowledge."
                        : "CONTEXT:\n" + context,
                message
        );
    }
}