package ai.fit.monk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MonkLogResponse {
    private String message;
    private List<String> aiFeedback;
}

