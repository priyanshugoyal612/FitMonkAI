package ai.fit.monk.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private UserDto user;
    private List<MonkDailyLog>  logs;
    private List<String> insights;
}
