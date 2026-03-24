package ai.fit.monk.tools;


import ai.fit.monk.model.MonkDailyLog;
import ai.fit.monk.repository.MonkDailyLogRepository;
import ai.fit.monk.service.MonkTrackerService;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MonkDatabaseTool {


    private final MonkTrackerService monkTrackerService;

    @Tool(name = "save_monk_log",
            description = "Saves the monk's daily discipline log to the database. " +
                    "The input need to be taken from the provided by users in the chat."
    )
    public MonkDailyLog saveMonkLog(@ToolParam(description = "Details of progress of daily monk mode") MonkDailyLog monkDailyLog) {
        try {

            System.out.println("going to db create the ticket");
            return monkTrackerService.saveLog(monkDailyLog);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Tool(description = "new log infor details with old log id")
    public MonkDailyLog updateMonkLog(@ToolParam(description = "Details of progress of daily monk mode") MonkDailyLog monkDailyLog) {
        return monkTrackerService.updateLog(monkDailyLog);

    }

    @Tool(description = "get the current time from system")
    public String getCurrentSystemTime() {
        return String.valueOf(System.currentTimeMillis());

    }

}
