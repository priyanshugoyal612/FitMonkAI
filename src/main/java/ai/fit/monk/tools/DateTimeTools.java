package ai.fit.monk.tools;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateTimeTools {

    @Tool(description = "Get current date in ISO format (yyyy-MM-dd)")
    public String getCurrentDate() {
        return LocalDate.now().toString();
    }
}