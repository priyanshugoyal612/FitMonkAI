package ai.fit.monk.model;

import lombok.Data;

@Data
public class MonkLogRequest {

    private int calories;
    private int steps;
    private boolean focus;
    private boolean noDopamine;
    private boolean workout;
    private String notes;
}
