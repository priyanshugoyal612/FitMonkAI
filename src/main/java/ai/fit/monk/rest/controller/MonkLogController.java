package ai.fit.monk.rest.controller;

import ai.fit.monk.model.MonkLogRequest;
import ai.fit.monk.model.MonkLogResponse;
import ai.fit.monk.model.User;
import ai.fit.monk.service.MonkLogService;
import ai.fit.monk.utility.UserUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class MonkLogController {

    private final MonkLogService monkLogService;
    private final UserUtilityService userUtilityService;

    @PostMapping
    public ResponseEntity<MonkLogResponse> saveLog(@RequestBody MonkLogRequest request) {
        User user = userUtilityService.getCurrentUser();
        MonkLogResponse response = monkLogService.saveLogWithFeedback(request, user);
        return ResponseEntity.ok(response);
    }
}
