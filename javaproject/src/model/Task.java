package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a study, project, or placement preparation task.
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String category; // DSA, Core CS, Projects, Aptitude, Resume/Prep
    private String priority; // High, Medium, Low
    private LocalDate dueDate;
    private double estimatedHours;
    private boolean completed;

    public Task(String title, String category, String priority, LocalDate dueDate, double estimatedHours) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.estimatedHours = estimatedHours;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return "[" + (completed ? "X" : " ") + "] (" + priority + ") " + title + " [" + category + "] Due: " + dueDate;
    }
}
