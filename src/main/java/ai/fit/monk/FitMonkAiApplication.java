package ai.fit.monk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FitMonkAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitMonkAiApplication.class, args);
	}

}
