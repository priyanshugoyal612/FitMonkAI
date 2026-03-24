package ai.fit.monk.config;

import java.util.List;

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

                new Document("""
                        Monk mode is a structured self-improvement phase that can be followed
                         for any period (such as 21, 30, 45, 60, or 90 days), where an individual focuses on
                          discipline, fitness, learning, and eliminating distractions to achieve personal transformation.
                        """),

                new Document("""
                        Monk Mode Routine:
                        - Sleep by 9:30 PM and wake up early (4-5 AM)
                        - Follow a strict daily routine
                        - Start the day with a cold shower
                        - Avoid unnecessary screen time
                        """),

                new Document("""
                        Monk Mode Discipline:
                        - Avoid cheap dopamine sources like social media, porn, and junk content
                        - Say no to distractions
                        - Stay consistent even when motivation is low
                        - Take full responsibility for your life
                        """),

                new Document("""
                        Fitness Rules:
                        - Strength training helps retain muscle during fat loss
                        - Daily exercise improves metabolism and mental clarity
                        - Walk at least 5000-10000 steps per day
                        """),

                new Document("""
                        Diet Rules:
                        - Protein intake should be 1.6 to 2.2g per kg body weight
                        - Maintain calorie deficit (300-500 calories) for fat loss
                        - Drink at least 3 liters of water daily
                        - Include fruits, vegetables, and whole foods
                        """),

                new Document("""
                        Workout Plan Basics:
                        - Perform strength training 4-5 times per week
                        - Include pushups, squats, and compound exercises
                        - Focus on progressive overload
                        """),

                new Document("""
                        Mental Growth:
                        - Read for at least 60 minutes daily
                        - Practice writing and reflection
                        - Focus on learning high-income skills like AI and technology
                        """),

                new Document("""
                        Environment:
                        - Surround yourself with positive and growth-oriented people
                        - Avoid negative or energy-draining individuals
                        """),

                new Document("""
                        Self Development:
                        - Improve communication skills
                        - Dress well and maintain hygiene
                        - Build confidence through action
                        """),

                new Document("""
                        Goal of Monk Mode:
                        The goal is to transform physically, mentally, and financially over a period
                        through discipline, consistency, and focused effort.
                        """)
        );
        vectorStore.add(docs);
    }
}