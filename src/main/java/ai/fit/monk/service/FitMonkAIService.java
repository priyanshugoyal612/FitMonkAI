package ai.fit.monk.service;

import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.model.User;
import ai.fit.monk.model.WeeklySummary;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.tools.MonkDatabaseTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public String getResponseFromFitMonk(String chat, User user, String conversationId) {

        // 🔥 1. Retrieve past logs
       // String ragContext = getRagContext(chat, user);

        // 🔥 2. Retrieve past conversations (memory)
       /* List<Document> memoryDocs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(chat)
                        .filterExpression(
                                "userId == '" + user.getUserId() + "' AND type == 'conversation'"
                        )
                        .topK(3)
                        .build()
        );

        String memoryContext = memoryDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String userContext = "User Info:\n" +
                "User Id: " + user.getUserId();

        String behaviorContext = "User is asking: " + chat;

        String systemPromptFinal = resolveSystemPrompt(
                ragContext,
                memoryContext,
                behaviorContext
        )*/;

        String basePrompt="";

        try {
             basePrompt = StreamUtils.copyToString(
                    systemPrompt.getInputStream(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String response= this.chatClient.prompt()
                .system(basePrompt )// + "\n" + userContext)
                .user(chat)
              //  .advisors(
                //        advisorSpec -> advisorSpec.param("conversationId", conversationId))
              //  .tools(monkDatabaseTool)
                .call()
                .content();

        // 🔥 3. Store conversation (memory)
        vectorStore.add(List.of(
                new Document(
                        "User: " + chat + "\nAI: " + response,
                        Map.of(
                                "userId", user.getId(),
                                "type", "conversation",
                                "date", LocalDate.now().toString()
                        )
                )
        ));

        return response;

    }



    @Cacheable(value = "weekly", key = "#user.id")
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

        String ragContext = getRagContext(ragQuery, user);

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

        int days = summary.logs().size();
        if (days == 0) {
            return "no streak data";
        }

        StringBuilder query = new StringBuilder();

        // Weakness signals
        if (summary.totalCalories() / days > 1500)
            query.append("high calories fat gain ");

        if ((double) summary.workoutDays() / days < .5)
            query.append("lack of exercise ");

        if (summary.totalSteps() / days < 6000)
            query.append("low activity ");

        if (summary.focusDays() < 3)
            query.append("low focus ");

        if (summary.averageScore() < 50)
            query.append("low discipline ");

        // Strength signals (VERY IMPORTANT)
        if ((double) summary.workoutDays() / days >= .7)
            query.append("consistent workouts ");

        if (summary.totalSteps() / days >= 8000)
            query.append("active lifestyle ");

        if (summary.focusDays() >= 5)
            query.append("high focus ");

        if (summary.averageScore() >= 75)
            query.append("high discipline ");

        // Fallback
        if (query.isEmpty()) {
            return "balanced routine discipline";
        }

        return query.toString().trim();
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

// Creating Rag Context
    public String getRagContext(String query, User user) {

        if (query == null || query.trim().isEmpty()) {
            return ""; // or return "No past context"
        }


        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .filterExpression(
                                "userId == '" + user.getUserId() + "'"
                        )
                        .topK(10)
                        .build()
        );

        // ✅ Sort by recent date
        docs = docs.stream()
                .sorted((d1, d2) -> {

                    String date1 = getSafeDate(d1);
                    String date2 = getSafeDate(d2);

                    return date2.compareTo(date1); // safe now
                })
                .limit(5)
                .toList();

        // ✅ Structured context (VERY IMPORTANT)
        return docs.stream()
                .map(doc -> String.format(
                        "- [%s] Score: %s → %s",
                        doc.getMetadata().get("date"),
                        doc.getMetadata().get("score"),
                        doc.getText()
                ))
                .collect(Collectors.joining("\n"));

    }


    //

    private String resolveSystemPrompt(
            String ragContext,
            String memoryContext,
            String behaviorContext
    ) {
        try {
            String basePrompt = StreamUtils.copyToString(
                    systemPrompt.getInputStream(),
                    StandardCharsets.UTF_8
            );

            return basePrompt + """

                ---------------------
                🔍 USER HISTORY (RAG)
                %s

                ---------------------
                🧠 MEMORY (PAST BEHAVIOR)
                %s

                ---------------------
                📊 CURRENT ANALYSIS
                %s

                ---------------------
                ⚠️ PRIORITY RULES

                1. ALWAYS use USER HISTORY before giving advice
                2. If repeated mistakes exist → call them out
                3. If user is improving → acknowledge briefly
                4. NEVER give generic advice
                5. Use real data from context

                """.formatted(
                    safe(ragContext),
                    safe(memoryContext),
                    safe(behaviorContext)
            );

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load fit_monk.st prompt", e);
        }
    }

    private String safe(String input) {
        return (input == null || input.isBlank())
                ? "No data available"
                : input;
    }


    private String getSafeDate(Document doc) {

        if (doc == null || doc.getMetadata() == null) {
            return "0000-00-00";
        }

        Object dateObj = doc.getMetadata().get("date");

        if (dateObj == null) {
            return "0000-00-00";
        }

        return dateObj.toString();
    }


}

