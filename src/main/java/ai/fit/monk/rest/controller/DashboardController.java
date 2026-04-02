package ai.fit.monk.rest.controller;

import ai.fit.monk.model.DashboardResponse;
import ai.fit.monk.model.User;
import ai.fit.monk.service.DashboardService;
import ai.fit.monk.utility.UserUtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;
    @Autowired
    private UserUtilityService userUtilityService;


    @GetMapping
    public DashboardResponse getDashboard()    {
        User user = userUtilityService.getCurrentUser();
        return service.getDashboard(user);
    }
}
