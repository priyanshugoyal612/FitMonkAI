package ai.fit.monk.service.orchestration;

import ai.fit.monk.enums.IntentType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntentClassifier {

    private final ChatClient chatClient;


    public IntentType classify(String message) {

        try {
            String result = chatClient.prompt()
                    .system("""
                            Classify intent into one of:
                            COACHING, LOGGING, ANALYSIS, QUERY, CASUAL
                            Return ONLY one word.
                            """)
                    .user(message)
                    .call()
                    .content();

            return IntentType.valueOf(result.trim().toUpperCase());

        } catch (Exception e) {
            return IntentType.COACHING;
        }
    }
}