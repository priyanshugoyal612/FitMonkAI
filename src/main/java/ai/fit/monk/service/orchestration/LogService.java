package ai.fit.monk.service.orchestration;


import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.User;
import ai.fit.monk.tools.MonkDatabaseTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final ChatClient chatClient;
    private final MonkDatabaseTool monkDatabaseTool;
    private final ObjectMapper objectMapper;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;

    public String handle(String message, User user, String conversationId) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();

        MessageChatMemoryAdvisor memoryAdvisor =
                MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId) // 🔥 THIS LINE FIXES EVERYTHING
                        .build();

        try {
            String json = chatClient.prompt()
                    .system("""
                            Extract JSON:
                            calories, workout, steps, learningHours, mood
                            """)
                    .user(message)
                    .advisors(a -> a
                            .advisors(memoryAdvisor)
                    )
                    .tools(monkDatabaseTool)
                    .call()

                    .content();

            //MonkDailyLog log = objectMapper.readValue(json, MonkDailyLog.class);

           // monkDatabaseTool.saveMonkLog(log);

            return "Log saved. Stay disciplined.";

        } catch (Exception e) {
            return "Failed to save log. Try again.";
        }
    }
}