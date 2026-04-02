package ai.fit.monk.rest.controller;


import java.time.LocalDate;
import java.util.List;

import ai.fit.monk.model.User;
import ai.fit.monk.model.WeeklySummary;
import ai.fit.monk.service.FitMonkAIService;

import ai.fit.monk.utility.UserUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fit/monk/ai")
@CrossOrigin("http://localhost:5173")
public class FitMonkAIController {

    private final FitMonkAIService fitMonkAIService;

    @Autowired
    private UserUtilityService userUtilityService;


    public FitMonkAIController(FitMonkAIService fitMonkAIService) {
        this.fitMonkAIService = fitMonkAIService;

    }


    @PostMapping("/chat")
    public ResponseEntity<String> fitMonk(@RequestBody String chat, @RequestHeader("conversationId") String conversationId) {
        User user = userUtilityService.getCurrentUser();
        return ResponseEntity.ok(fitMonkAIService.getResponseFromFitMonk(chat, user, conversationId));
    }


    @GetMapping("/report/weekly")
    public ResponseEntity<String> getWeeklyReport() {
        User user = userUtilityService.getCurrentUser();
        return ResponseEntity.ok(fitMonkAIService.getWeeklyReport(user));
    }

    @GetMapping("/coach/advice")
    public String getAdvice() {
        User user = userUtilityService.getCurrentUser();
        return fitMonkAIService.generatePersonalizedAdvice(user);
    }

}
