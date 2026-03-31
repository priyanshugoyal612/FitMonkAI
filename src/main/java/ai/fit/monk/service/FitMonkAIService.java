package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.User;
import ai.fit.monk.model.WeeklySummary;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.tools.MonkDatabaseTool;
import lombok.RequiredArgsConstructor;


import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FitMonkAIService {

    private Logger logger = LoggerFactory.getLogger(FitMonkAIService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final MonkDailyLogRepository monkDailyLogRepository;

    private final MonkDatabaseTool monkDatabaseTool;

    @Autowired
    private PatternDetectionService patternService;
    @Autowired
    private MonkMemoryService memoryService;

    @Value("classpath:/fit_monk.st")
    private Resource systemPrompt;

    public String getResponseFromFitMonk(String chat, User user , String conversationId ) {

        //List<Document> docs = vectorStore.similaritySearch(chat);

        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(chat)
                        .filterExpression("userId == '" + user.getId() + "'")
                        .topK(5)
                        .build()
        );
        String context = docs.stream().limit(5)
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);

        String userContext = "User Info:\n" +
                "User Id: " + user.getUserId();

        return this.chatClient.prompt()
                .system(resolveSystemPrompt(context) + "\n" + userContext )
                .user(chat)
                .advisors(
                        advisorSpec -> advisorSpec.param("conversationId",conversationId))
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


    public String getWeeklyReport(User user) {
        LocalDate reportEndDate = LocalDate.now();
        LocalDate reportStartDate = reportEndDate.minusDays(6);

        List<MonkDailyLog> logs = monkDailyLogRepository
                .findByUserAndLogDateBetweenOrderByLogDateAsc(user, reportStartDate, reportEndDate);
        if (logs.isEmpty()) {
            return
                    "No logs found for this week.";
        }

        WeeklySummary weeklySummary = getSummary(user, logs, reportStartDate, reportEndDate);

        String ragQuery = buildRagQuery(weeklySummary);
        String ragContext = getRagContext(ragQuery);

        return chatClient.prompt()
                .system("""
                        You are FIT_MONK_AI, a strict monk mode coach.
                        
                        Your personality:
                                                            - Direct, sharp, no excuses
                                                            - No corporate tone
                                                            - No long explanations
                                                            - No fluff
                        
                                                            Rules:
                                                            - Keep response short (max 8–12 lines)
                                                            - Focus on discipline, not motivation
                                                            - Call out weaknesses clearly
                                                            - Give 2–3 actionable steps only
                        
                                                            Format:
                        
                                                            🔥 Weekly Report
                        
                                                            Score: X/100 \s
                                                            Streak: X days \s
                        
                                                            Strength:
                                                            - ...
                        
                                                            Weakness:
                                                            - ...
                        
                                                            Action:
                                                            - ...
                        
                                                            Tone examples:
                                                            - "Good discipline. Maintain it."
                                                            - "You are slipping. Fix this."
                                                            - "Not enough. Push harder."
                        
                                                            DO NOT:
                                                            - Write long paragraphs
                                                            - Give generic advice
                                                            - Use soft language
                        
                                                            Your goal:
                                                            Drive discipline, not comfort.
                        """)
                .user("""
                        Knowledge:
                        %s
                        
                        Weekly Summary:
                        Days: %d
                        Total Calories: %d
                        Total Focus Days: %d
                        Total No of no dopamine Days: %d
                        Total Steps: %d
                        Total Workout: %d
                        Avg Focus: %d
                        Daily learning notes :%s
              
                       Current Streak: %d
                        Avg Score: %.2f
                        """.formatted(
                        ragContext,
                        weeklySummary.currentStreak(),
                        weeklySummary.totalCalories(),
                        weeklySummary.focusDays(),
                        weeklySummary.noDopamineDays(),
                        weeklySummary.totalSteps(),
                        weeklySummary.workoutDays(),
                        weeklySummary.focusDays(),
                        weeklySummary.notes().stream().filter(java.util.Objects::nonNull).toList(),
                        weeklySummary.currentStreak(),
                        weeklySummary.averageScore()


                ))
                .call()
                .content();
    }


    private static WeeklySummary getSummary(User user, List<MonkDailyLog> logs, LocalDate reportStartDate, LocalDate reportEndDate) {
        int totalCalories = logs.stream()
                .mapToInt(MonkDailyLog::getCaloriesIntake)
                .sum();

        int totalSteps = logs.stream()
                .mapToInt(MonkDailyLog::getDailySteps)
                .sum();

        int workoutDays = (int) logs.stream()
                .filter(MonkDailyLog::isWorkoutDone)
                .count();

        int focusDays = (int) logs.stream()
                .filter(MonkDailyLog::isFocusHours)
                .count();

        int noDopamineDays = (int) logs.stream()
                .filter(MonkDailyLog::isNoDopamine)
                .count();

        double averageScore = logs.stream()
                .mapToInt(MonkDailyLog::getScore)
                .average()
                .orElse(0.0);

        int currentStreak = logs.isEmpty()
                ? 0
                : logs.get(logs.size() - 1).getStreak();

        List<String> notes = logs.stream()
                .map(MonkDailyLog::getNotes)
                .toList();

        return new WeeklySummary(
                user.getUserId(),
                reportStartDate,
                reportEndDate,
                logs.size(),
                totalCalories,
                totalSteps,
                workoutDays,
                focusDays,
                noDopamineDays,
                averageScore,
                currentStreak,
                notes,
                logs,
                ""
        );
    }

    public String buildRagQuery(WeeklySummary summary) {

        StringBuilder query = new StringBuilder();

        int days=summary.logs().size();
        if (days > 0) {
            return "no streak data";
        }

        if (summary.totalCalories()/days > 1500)
            query.append("high calories fat gain ");

        if ((double) summary.workoutDays() /days < .5)
            query.append("lack of exercise impact ");

        if (summary.totalSteps()/days < 6000)
            query.append("low steps activity health ");

        if (summary.focusDays() < 3)
            query.append("low focus productivity ");

        if (summary.averageScore() < 50)
            query.append("very less score ");

        return query.toString();
    }

    public String getRagContext(String query) {

        List<Document> docs = vectorStore.similaritySearch(query);

        return docs.stream()
                .map(Document::getText)
                .reduce("", (a, b) -> a + "\n" + b);
    }


    public String generatePersonalizedAdvice(User user) {

        List<MonkDailyLog> logs = monkDailyLogRepository.findTop7ByUserOrderByLogDateDesc(user);

        String patterns = patternService.detectPatterns(logs);

        List<Document> similarFailures = memoryService.findSimilarFailures(user.getUserId());

        String memoryContext = similarFailures.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String prompt = """
    You are a strict Monk AI Coach.

    USER HISTORY:
    %s

    PATTERNS:
    %s

    SIMILAR FAILURE CASES:
    %s

    Give precise advice for tomorrow.
    """.formatted(logs, patterns, memoryContext);

        return chatClient.prompt(prompt).call().content();
    }
}

