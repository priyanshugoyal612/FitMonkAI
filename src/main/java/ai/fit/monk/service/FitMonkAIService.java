package ai.fit.monk.service;

import ai.fit.monk.tools.MonkDatabaseTool;
import lombok.RequiredArgsConstructor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FitMonkAIService {

    private Logger logger= LoggerFactory.getLogger(FitMonkAIService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    private final MonkDatabaseTool monkDatabaseTool;

    @Value("classpath:/fit_monk.st")
    private Resource systemPrompt;

    public String getResponseFromFitMonk(String chat, String conversationId) {

        List<Document> docs = vectorStore.similaritySearch(chat);
        String context = docs.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        return this.chatClient.prompt().system(resolveSystemPrompt(context))
                .user(chat)
                .advisors(
                        advisorSpec -> advisorSpec.param("conversationId", conversationId))
                .tools(monkDatabaseTool)
                .call()
                .content();
    }

    private String resolveSystemPrompt(String context) {
        try {
            String promptTemplate = StreamUtils.copyToString(systemPrompt.getInputStream(), StandardCharsets.UTF_8);
            return promptTemplate + "\n\nContext:\n" + context;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load fit_monk.st prompt", e);
        }
    }

}

