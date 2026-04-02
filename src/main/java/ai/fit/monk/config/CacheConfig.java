package ai.fit.monk.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("dashboard","weekly");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(100, TimeUnit.MINUTES) // 🔥 10 min cache
                        .maximumSize(1000)
        );

        return cacheManager;
    }
}
