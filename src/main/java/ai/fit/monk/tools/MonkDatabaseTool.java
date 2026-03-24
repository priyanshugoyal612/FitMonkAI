package ai.fit.monk.tools;


import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.service.MonkTrackerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class MonkDatabaseTool {




    private final MonkTrackerService monkTrackerService;

    @Tool(name = "save_monk_log",
            description = "Saves the monk's daily discipline log to the database. " +
                    "The input need to be taken from the provided by users in the chat."
    )
    public MonkDailyLog saveMonkLog(@ToolParam(description = "Details of progress of daily monk mode") MonkDailyLog monkDailyLog) {
        try {

            log.info("going to db create the log in db");
            log.info("Incoming monkDailyLog: {}", monkDailyLog);
            return monkTrackerService.saveLog(monkDailyLog);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Tool(description = "new log information details with old log id")
    public MonkDailyLog updateMonkLog(@ToolParam(description = "Details of progress of daily monk mode") MonkDailyLog monkDailyLog) {

        return monkTrackerService.updateLog(monkDailyLog);

    }

    @Tool(description = "get the current time from system")
    public String getCurrentSystemTime() {
        return String.valueOf(System.currentTimeMillis());

    }

}
