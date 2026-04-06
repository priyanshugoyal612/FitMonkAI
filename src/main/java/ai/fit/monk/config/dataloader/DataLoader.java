package ai.fit.monk.config.dataloader;

import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final VectorStore vectorStore;

    public DataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    //@PostConstruct
    public void load() {

        List<Document> docs = List.of(

                new Document(
                        "Monk mode is a structured self-improvement phase focused on discipline, fitness, learning, and eliminating distractions for personal transformation.",
                        Map.of(
                                "id", "monk_def_1",
                                "type", "knowledge",
                                "topic", "monk_mode",
                                "subtopic", "definition",
                                "importance", 0.95,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Sleep by 9:30 PM, wake up early, follow a strict routine, start the day with a cold shower, and avoid unnecessary screen time.",
                        Map.of(
                                "id", "routine_1",
                                "type", "knowledge",
                                "topic", "routine",
                                "subtopic", "daily_habits",
                                "importance", 0.9,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Avoid cheap dopamine sources like social media, porn, and junk content. Stay consistent and take full responsibility for your life.",
                        Map.of(
                                "id", "discipline_1",
                                "type", "rule",
                                "topic", "discipline",
                                "subtopic", "dopamine_control",
                                "importance", 1.0,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Strength training helps retain muscle during fat loss. Daily exercise improves metabolism and mental clarity. Walk at least 8000 steps daily.",
                        Map.of(
                                "id", "fitness_1",
                                "type", "knowledge",
                                "topic", "fitness",
                                "subtopic", "general",
                                "importance", 0.9,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Protein intake should be between 1.6 to 2.2 grams per kg body weight. Maintain a calorie deficit of 300-500 calories for fat loss.",
                        Map.of(
                                "id", "diet_1",
                                "type", "knowledge",
                                "topic", "diet",
                                "subtopic", "nutrition",
                                "importance", 0.95,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Drink at least 3 liters of water daily and include fruits, vegetables, and whole foods in your diet.",
                        Map.of(
                                "id", "diet_2",
                                "type", "knowledge",
                                "topic", "diet",
                                "subtopic", "hydration",
                                "importance", 0.85,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Perform strength training 4-5 times per week. Focus on compound exercises like pushups, squats, and progressive overload.",
                        Map.of(
                                "id", "workout_1",
                                "type", "knowledge",
                                "topic", "fitness",
                                "subtopic", "workout_plan",
                                "importance", 0.9,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Read for at least 60 minutes daily. Practice writing and reflection. Focus on high-income skills like AI and technology.",
                        Map.of(
                                "id", "mental_1",
                                "type", "knowledge",
                                "topic", "mental_growth",
                                "subtopic", "learning",
                                "importance", 0.9,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Surround yourself with positive and growth-oriented people. Avoid negative or energy-draining individuals.",
                        Map.of(
                                "id", "env_1",
                                "type", "knowledge",
                                "topic", "environment",
                                "subtopic", "social",
                                "importance", 0.85,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "Improve communication skills, maintain hygiene, dress well, and build confidence through consistent action.",
                        Map.of(
                                "id", "self_dev_1",
                                "type", "knowledge",
                                "topic", "self_development",
                                "subtopic", "confidence",
                                "importance", 0.85,
                                "version", 1,
                                "source", "system"
                        )
                ),

                new Document(
                        "The goal of monk mode is to achieve physical, mental, and financial transformation through discipline and consistency.",
                        Map.of(
                                "id", "goal_1",
                                "type", "knowledge",
                                "topic", "monk_mode",
                                "subtopic", "goal",
                                "importance", 1.0,
                                "version", 1,
                                "source", "system"
                        )
                )
        );

        vectorStore.add(docs);
    }

}