package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a logged coding / DSA practice session.
 */
public class CodingLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String platform;      // LeetCode, HackerRank, Codeforces, GeeksforGeeks, etc.
    private String topic;         // Arrays, Trees, Graphs, Dynamic Programming, etc.
    private String difficulty;    // Easy, Medium, Hard
    private int problemsSolved;
    private int durationMinutes;
    private LocalDate logDate;
    private String notes;

    public CodingLog(String platform, String topic, String difficulty, int problemsSolved, int durationMinutes, LocalDate logDate, String notes) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.platform = platform;
        this.topic = topic;
        this.difficulty = difficulty;
        this.problemsSolved = problemsSolved;
        this.durationMinutes = durationMinutes;
        this.logDate = logDate;
        this.notes = notes;
    }

    public String getId() { return id; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getProblemsSolved() { return problemsSolved; }
    public void setProblemsSolved(int problemsSolved) { this.problemsSolved = problemsSolved; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return logDate + " | " + platform + " (" + topic + " - " + difficulty + "): " + problemsSolved + " problems in " + durationMinutes + " mins";
    }
}
