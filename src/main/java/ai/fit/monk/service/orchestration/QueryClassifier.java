package ai.fit.monk.service.orchestration;

import ai.fit.monk.enums.QueryType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryClassifier {

    private final ChatClient chatClient;

    public QueryType classify(String message) {

        try {
            String result = chatClient.prompt()
                    .system("""
                        Classify:
                        GENERAL → definition/explanation/enquiry/general
                        CONTEXTUAL → only return when needs user history

                        Return ONLY: GENERAL or CONTEXTUAL
                    """)
                    .user(message)
                    .call()
                    .content();

            return QueryType.valueOf(result.trim().toUpperCase());

        } catch (Exception e) {
            return QueryType.GENERAL;
        }
    }
}