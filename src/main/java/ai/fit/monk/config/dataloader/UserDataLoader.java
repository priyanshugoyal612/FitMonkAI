package ai.fit.monk.config.dataloader;

import ai.fit.monk.model.User;
import ai.fit.monk.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void load() {

        if (userRepository.findByEmail("admin@fitmonk.com").isEmpty()) {

            User user = new User();
            user.setEmail("admin@fitmonk.com");
            user.setUserId("admin@fitmonk");
            user.setPassword(encoder.encode("123456"));
            user.setName("Admin User");
            user.setRole("ADMIN");
            user.setStreaks(0L);
            userRepository.save(user);
            System.out.println("Default user created");
        }
    }
}
