package ai.fit.monk.service.orchestration;

import ai.fit.monk.model.User;
import ai.fit.monk.tools.DateTimeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final ChatClient chatClient;

    private final DateTimeTools dateTimeTools;

    public String handle(String message, User user, String conversationId) {

        String prompt = """
                You are a strict Monk Mode Coach. Answer the user query

                USER ID:
                %s

                MESSAGE:
                %s

                Be strict. No excuses. Keep it short.
                """.formatted(user.getUserId(), message);

        return chatClient.prompt()
                .system(prompt)
                .advisors(a -> a.param("conversationId", conversationId))
                .tools(dateTimeTools)
                .call()
                .content();
    }
}