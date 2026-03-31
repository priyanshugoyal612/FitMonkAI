package ai.fit.monk.utility;

import ai.fit.monk.model.User;
import ai.fit.monk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtilityService {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
