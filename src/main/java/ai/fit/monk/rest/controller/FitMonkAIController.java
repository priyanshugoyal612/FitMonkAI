package ai.fit.monk.rest.controller;


import java.time.LocalDate;
import java.util.List;

import ai.fit.monk.rest.dto.DailyDisciplineLogRequest;
import ai.fit.monk.rest.dto.DailyDisciplineLogResponse;
import ai.fit.monk.rest.dto.DisciplineFeedbackResponse;
import ai.fit.monk.service.DisciplineTrackerService;
import ai.fit.monk.service.FitMonkAIService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fit/monk/ai")
public class FitMonkAIController {

    private final FitMonkAIService fitMonkAIService;
    private final DisciplineTrackerService disciplineTrackerService;

    public FitMonkAIController(FitMonkAIService fitMonkAIService, DisciplineTrackerService disciplineTrackerService) {
        this.fitMonkAIService = fitMonkAIService;
        this.disciplineTrackerService = disciplineTrackerService;
    }



    @PostMapping("/chat")
    public ResponseEntity<String> fitMonk(@RequestBody String chat, @RequestHeader("conversationId") String conversationId) {
        return ResponseEntity.ok(fitMonkAIService.getResponseFromFitMonk(chat, conversationId));
    }

    @PostMapping("/discipline/log")
    public ResponseEntity<DailyDisciplineLogResponse> saveDailyLog(@RequestBody DailyDisciplineLogRequest request) {
        return ResponseEntity.ok(disciplineTrackerService.upsertDailyLog(request));
    }

    @GetMapping("/discipline/log")
    public ResponseEntity<DailyDisciplineLogResponse> getDailyLog(@RequestParam String userId, @RequestParam LocalDate logDate) {
        return ResponseEntity.ok(disciplineTrackerService.getDailyLog(userId, logDate));
    }

    @GetMapping("/discipline/logs")
    public ResponseEntity<List<DailyDisciplineLogResponse>> getLogs(@RequestParam String userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(disciplineTrackerService.getLogs(userId, startDate, endDate));
    }

    @PostMapping("/discipline/feedback")
    public ResponseEntity<DisciplineFeedbackResponse> getDisciplineFeedback(
            @RequestParam String userId,
            @RequestParam LocalDate logDate,
            @RequestHeader(value = "conversationId", defaultValue = "discipline-feedback") String conversationId) {

        DailyDisciplineLogResponse log = disciplineTrackerService.getDailyLog(userId, logDate);
        List<DailyDisciplineLogResponse> recentLogs = disciplineTrackerService.getRecentLogs(userId, logDate, 7);
        String feedback = fitMonkAIService.generateDisciplineFeedback(log, recentLogs, conversationId);

        return ResponseEntity.ok(new DisciplineFeedbackResponse(log, feedback));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }



}
