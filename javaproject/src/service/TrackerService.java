package service;

import model.CodingLog;
import model.PlacementApplication;
import model.StudentProfile;
import model.Task;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core business logic and analytics service for the tracker.
 */
public class TrackerService {
    private StudentProfile profile;
    private final List<Task> tasks;
    private final List<CodingLog> codingLogs;
    private final List<PlacementApplication> applications;
    private final List<Runnable> changeListeners;

    public TrackerService() {
        StorageData data = StorageService.loadData();
        this.profile = data.getProfile();
        this.tasks = new ArrayList<>(data.getTasks());
        this.codingLogs = new ArrayList<>(data.getCodingLogs());
        this.applications = new ArrayList<>(data.getApplications());
        this.changeListeners = new ArrayList<>();
    }

    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyChange() {
        save();
        for (Runnable r : changeListeners) {
            try {
                r.run();
            } catch (Exception e) {
                // Ignore listener exceptions
            }
        }
    }

    public void save() {
        StorageData data = new StorageData(profile, tasks, codingLogs, applications);
        StorageService.saveData(data);
    }

    // --- Profile Management ---
    public StudentProfile getProfile() {
        return profile;
    }

    public void updateProfile(StudentProfile updated) {
        this.profile = updated;
        notifyChange();
    }

    // --- Task Operations ---
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public List<Task> getPendingTasks() {
        return tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList());
    }

    public void addTask(Task task) {
        tasks.add(0, task);
        notifyChange();
    }

    public void removeTask(String taskId) {
        tasks.removeIf(t -> t.getId().equals(taskId));
        notifyChange();
    }

    public void toggleTaskCompletion(String taskId) {
        for (Task t : tasks) {
            if (t.getId().equals(taskId)) {
                t.setCompleted(!t.isCompleted());
                break;
            }
        }
        notifyChange();
    }

    // --- Coding Log Operations ---
    public List<CodingLog> getCodingLogs() {
        return Collections.unmodifiableList(codingLogs);
    }

    public void addCodingLog(CodingLog log) {
        codingLogs.add(0, log);
        notifyChange();
    }

    public void removeCodingLog(String logId) {
        codingLogs.removeIf(l -> l.getId().equals(logId));
        notifyChange();
    }

    public int getTotalProblemsSolved() {
        return codingLogs.stream().mapToInt(CodingLog::getProblemsSolved).sum();
    }

    public int getTotalPracticeMinutes() {
        return codingLogs.stream().mapToInt(CodingLog::getDurationMinutes).sum();
    }

    public Map<String, Integer> getProblemsByTopic() {
        Map<String, Integer> map = new TreeMap<>();
        for (CodingLog log : codingLogs) {
            map.put(log.getTopic(), map.getOrDefault(log.getTopic(), 0) + log.getProblemsSolved());
        }
        return map;
    }

    public Map<String, Integer> getProblemsByPlatform() {
        Map<String, Integer> map = new TreeMap<>();
        for (CodingLog log : codingLogs) {
            map.put(log.getPlatform(), map.getOrDefault(log.getPlatform(), 0) + log.getProblemsSolved());
        }
        return map;
    }

    // --- Placement Application Operations ---
    public List<PlacementApplication> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    public void addApplication(PlacementApplication app) {
        applications.add(0, app);
        notifyChange();
    }

    public void updateApplicationStatus(String appId, PlacementApplication.Status newStatus) {
        for (PlacementApplication app : applications) {
            if (app.getId().equals(appId)) {
                app.setStatus(newStatus);
                break;
            }
        }
        notifyChange();
    }

    public void removeApplication(String appId) {
        applications.removeIf(a -> a.getId().equals(appId));
        notifyChange();
    }

    public int getActiveApplicationsCount() {
        return (int) applications.stream()
                .filter(a -> a.getStatus() != PlacementApplication.Status.REJECTED 
                          && a.getStatus() != PlacementApplication.Status.OFFER_RECEIVED)
                .count();
    }

    public int getOffersCount() {
        return (int) applications.stream()
                .filter(a -> a.getStatus() == PlacementApplication.Status.OFFER_RECEIVED)
                .count();
    }

    // --- Analytics & Readiness Scoring ---
    public double getTaskCompletionRate() {
        if (tasks.isEmpty()) return 100.0;
        long completed = tasks.stream().filter(Task::isCompleted).count();
        return (completed * 100.0) / tasks.size();
    }

    public double getCgpaScore() {
        // CGPA out of 10 mapped to max 25 points
        double cgpa = Math.min(10.0, Math.max(0.0, profile.getCurrentCgpa()));
        return (cgpa / 10.0) * 25.0;
    }

    public double getDsaScore() {
        // DSA problems solved vs target mapped to max 35 points
        int target = Math.max(1, profile.getTargetDsaCount());
        int solved = getTotalProblemsSolved();
        double ratio = Math.min(1.0, (double) solved / target);
        return ratio * 35.0;
    }

    public double getTaskScore() {
        // Task completion percentage mapped to max 20 points
        return (getTaskCompletionRate() / 100.0) * 20.0;
    }

    public double getPipelineScore() {
        // Active applications & offers mapped to max 20 points
        int active = getActiveApplicationsCount();
        int offers = getOffersCount();
        double score = (active * 3.0) + (offers * 10.0);
        return Math.min(20.0, score);
    }

    public int calculateReadinessScore() {
        double total = getCgpaScore() + getDsaScore() + getTaskScore() + getPipelineScore();
        return (int) Math.round(Math.min(100.0, Math.max(0.0, total)));
    }

    public String getReadinessVerdict(int score) {
        if (score >= 85) return "Placement Ready (Tier 1 / Product Companies)";
        if (score >= 70) return "Strong Contender (Target High CTC Drives)";
        if (score >= 50) return "Developing (Focus on DSA & Mock Tests)";
        return "Needs Immediate Action (Boost Problem Solving & Academics)";
    }
}
