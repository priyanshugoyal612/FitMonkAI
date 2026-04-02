package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AIInsightService {

    private Logger logger = LoggerFactory.getLogger(AIInsightService.class);

    private final ChatClient chatClient;


    public List<String> generateInsights(List<MonkDailyLog> logs) {

        String logsText = logs.stream()
                .map(log -> String.format(
                        "Date: %s, Calories: %d, Workout: %s, Steps: %s, Focus: %s, Score: %d",
                        log.getLogDate(),
                        log.getCaloriesIntake(),
                        log.isWorkoutDone(),
                        log.getDailySteps(),
                        log.isFocusHours(),
                        log.getScore()
                ))
                .collect(Collectors.joining("\n"));

        String prompt = """
You are a strict monk discipline coach.

Give:
- 3 short insights
- 1 warning
- 1 goal

Rules:
- Each line should be separate
- No JSON
- No markdown
- No numbering
- Keep it clean

Example:
Calories are stable
Workout consistency is strong
Focus is improving
⚠️ Weekend discipline drops
🎯 Maintain 5-day workout streak

Logs:
%s
""".formatted(logsText);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 🔥 CLEAN + SPLIT
        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }


}