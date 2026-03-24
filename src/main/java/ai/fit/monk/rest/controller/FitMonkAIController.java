package ai.fit.monk.rest.controller;


import java.time.LocalDate;
import java.util.List;

import ai.fit.monk.model.WeeklySummary;
import ai.fit.monk.service.FitMonkAIService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fit/monk/ai")
public class FitMonkAIController {

    private final FitMonkAIService fitMonkAIService;


    public FitMonkAIController(FitMonkAIService fitMonkAIService) {
        this.fitMonkAIService = fitMonkAIService;

    }


    @PostMapping("/chat")
    public ResponseEntity<String> fitMonk(@RequestBody String chat, @RequestHeader("conversationId") String conversationId) {
        return ResponseEntity.ok(fitMonkAIService.getResponseFromFitMonk(chat, conversationId));
    }


    @GetMapping("/report/weekly")
    public ResponseEntity<String> getWeeklyReport(
            @RequestParam String userId) {

        return ResponseEntity.ok(fitMonkAIService.getWeeklyReport(userId));
    }

}
