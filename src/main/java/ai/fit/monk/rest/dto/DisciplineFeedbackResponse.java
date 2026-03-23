package ai.fit.monk.rest.dto;

public record DisciplineFeedbackResponse(
        DailyDisciplineLogResponse log,
        String feedback) {
}

