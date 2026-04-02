package ai.fit.monk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String name;
    private int streak;
    private int score;
    private String userId;
}