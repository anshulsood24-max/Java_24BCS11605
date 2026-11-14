package service;

import model.CodingLog;
import model.PlacementApplication;
import model.StudentProfile;
import model.Task;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for persisting application data and exporting reports.
 */
public class StorageService {
    private static final String DATA_FILE = "student_tracker_data.dat";

    public static StorageData loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof StorageData) {
                    return (StorageData) obj;
                }
            } catch (Exception e) {
                System.err.println("Could not load existing data: " + e.getMessage() + ". Creating default dataset.");
            }
        }
        return createDefaultData();
    }

    public static boolean saveData(StorageData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            return false;
        }
    }

    public static File exportSummaryReport(TrackerService tracker, String targetPath) throws IOException {
        File targetFile = (targetPath != null && !targetPath.trim().isEmpty()) 
                ? new File(targetPath) 
                : new File("Placement_Readiness_Report.txt");

        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(75);
        String subline = "-".repeat(75);

        StudentProfile p = tracker.getProfile();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy");

        sb.append(line).append("\n");
        sb.append("         STUDENT PRODUCTIVITY & PLACEMENT READINESS REPORT\n");
        sb.append("                  Generated on: ").append(LocalDate.now().format(dtf)).append("\n");
        sb.append(line).append("\n\n");

        sb.append("1. STUDENT PROFILE\n");
        sb.append(subline).append("\n");
        sb.append(String.format(" Name               : %s\n", p.getName()));
        sb.append(String.format(" Roll Number        : %s\n", p.getRollNumber()));
        sb.append(String.format(" Department         : %s\n", p.getDepartment()));
        sb.append(String.format(" Current CGPA       : %.2f / 10.0\n", p.getCurrentCgpa()));
        sb.append(String.format(" Target Role        : %s\n", p.getTargetRole()));
        sb.append(String.format(" DSA Target Goal    : %d solved problems\n", p.getTargetDsaCount()));
        sb.append(String.format(" Weekly Study Goal  : %d hours/week\n", p.getTargetWeeklyStudyHours()));
        sb.append("\n");

        int readiness = tracker.calculateReadinessScore();
        sb.append("2. READINESS SCORE & BENCHMARK\n");
        sb.append(subline).append("\n");
        sb.append(String.format(" Overall Readiness Score : %d%% [%s]\n", readiness, tracker.getReadinessVerdict(readiness)));
        sb.append(" Assessment Breakdown:\n");
        sb.append(String.format("  - CGPA Standing      : %.1f / 25 pts\n", tracker.getCgpaScore()));
        sb.append(String.format("  - DSA / Problem Solve: %.1f / 35 pts (Solved %d / %d)\n", 
                tracker.getDsaScore(), tracker.getTotalProblemsSolved(), p.getTargetDsaCount()));
        sb.append(String.format("  - Task Execution Rate: %.1f / 20 pts (%d%% completed)\n", 
                tracker.getTaskScore(), (int) tracker.getTaskCompletionRate()));
        sb.append(String.format("  - Placement Pipeline : %.1f / 20 pts (%d active, %d offers)\n", 
                tracker.getPipelineScore(), tracker.getActiveApplicationsCount(), tracker.getOffersCount()));
        sb.append("\n");

        sb.append("3. DSA & CODING PRACTICE SUMMARY\n");
        sb.append(subline).append("\n");
        sb.append(String.format(" Total Problems Solved : %d\n", tracker.getTotalProblemsSolved()));
        sb.append(String.format(" Total Practice Time   : %.1f hours\n", tracker.getTotalPracticeMinutes() / 60.0));
        sb.append(" Breakdown by Topic:\n");
        tracker.getProblemsByTopic().forEach((topic, count) -> 
            sb.append(String.format("   \u2022 %-20s : %d problems\n", topic, count))
        );
        sb.append("\n");

        sb.append("4. PENDING PRODUCTIVITY TASKS\n");
        sb.append(subline).append("\n");
        List<Task> pendingTasks = tracker.getPendingTasks();
        if (pendingTasks.isEmpty()) {
            sb.append("   All tasks completed! Excellent work.\n");
        } else {
            for (Task t : pendingTasks) {
                sb.append(String.format("   [%s] %-30s | Category: %-12s | Due: %s\n", 
                        t.getPriority(), t.getTitle(), t.getCategory(), t.getDueDate().format(dtf)));
            }
        }
        sb.append("\n");

        sb.append("5. PLACEMENT APPLICATIONS PIPELINE\n");
        sb.append(subline).append("\n");
        List<PlacementApplication> apps = tracker.getApplications();
        if (apps.isEmpty()) {
            sb.append("   No company drives added yet.\n");
        } else {
            for (PlacementApplication app : apps) {
                String nextRound = (app.getNextRoundDate() != null) ? app.getNextRoundDate().format(dtf) : "TBD";
                sb.append(String.format("   %-20s | %-18s | CTC: %5.1f LPA | Status: %-16s | Next: %s\n",
                        app.getCompanyName(), app.getRole(), app.getCtcLpa(), app.getStatus().getDisplayName(), nextRound));
            }
        }
        sb.append("\n");
        sb.append(line).append("\n");
        sb.append(" End of Report. Keep coding, stay consistent, and secure your dream offer!\n");
        sb.append(line).append("\n");

        Files.writeString(targetFile.toPath(), sb.toString());
        return targetFile;
    }

    private static StorageData createDefaultData() {
        StudentProfile profile = new StudentProfile(
                "Aryan Sharma",
                "2023CSB1045",
                "Computer Science & Engineering",
                8.75,
                "SDE 1 / Full Stack Engineer",
                250,
                24
        );

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Revise Graphs & Dijkstra's Algorithm", "DSA", "High", LocalDate.now().plusDays(2), 3.0));
        tasks.add(new Task("Review OS Process Scheduling & Deadlocks", "Core CS", "High", LocalDate.now().plusDays(3), 2.5));
        tasks.add(new Task("Implement Redis caching in Backend Project", "Projects", "Medium", LocalDate.now().plusDays(5), 4.0));
        tasks.add(new Task("Prepare Elevator Pitch & HR Behavioral Questions", "Resume/Prep", "Medium", LocalDate.now().plusDays(4), 2.0));
        tasks.add(new Task("Practice Quantitative Aptitude (Permutations & Probability)", "Aptitude", "Low", LocalDate.now().plusDays(7), 2.0));
        Task doneTask = new Task("Update Resume with GitHub Project Links", "Resume/Prep", "High", LocalDate.now().minusDays(1), 1.5);
        doneTask.setCompleted(true);
        tasks.add(doneTask);

        List<CodingLog> codingLogs = new ArrayList<>();
        codingLogs.add(new CodingLog("LeetCode", "Dynamic Programming", "Medium", 4, 120, LocalDate.now().minusDays(3), "Knapsack & Subsequence patterns"));
        codingLogs.add(new CodingLog("LeetCode", "Trees & BST", "Medium", 5, 90, LocalDate.now().minusDays(2), "LCA and Diameter of Tree"));
        codingLogs.add(new CodingLog("Codeforces", "Greedy Algorithms", "Hard", 2, 80, LocalDate.now().minusDays(1), "Div 2 Contest practice"));
        codingLogs.add(new CodingLog("GeeksforGeeks", "DBMS & SQL Queries", "Medium", 6, 60, LocalDate.now(), "Window functions & Joins"));
        codingLogs.add(new CodingLog("LeetCode", "Graphs", "Medium", 3, 75, LocalDate.now(), "BFS/DFS Topological Sort"));

        List<PlacementApplication> applications = new ArrayList<>();
        applications.add(new PlacementApplication("Google", "Software Engineer Intern", 28.0, 
                PlacementApplication.Status.OA_SCHEDULED, LocalDate.now().minusDays(10), LocalDate.now().plusDays(3), "Online Coding Assessment on HackerEarth", "Focus on clean code and time complexity."));
        applications.add(new PlacementApplication("Microsoft", "Support & SDE", 21.0, 
                PlacementApplication.Status.APPLIED, LocalDate.now().minusDays(5), null, "Applied via campus portal", "Resume shortlisted."));
        applications.add(new PlacementApplication("Amazon", "SDE 1", 24.5, 
                PlacementApplication.Status.TECH_INTERVIEW, LocalDate.now().minusDays(15), LocalDate.now().plusDays(6), "Technical Round 1: DSA + OOPs", "Prepare Leadership Principles."));
        applications.add(new PlacementApplication("Atlassian", "Associate Software Engineer", 25.0, 
                PlacementApplication.Status.OFFER_RECEIVED, LocalDate.now().minusDays(25), null, "Offer letter received!", "Base: 16 LPA + RSUs + Joining Bonus"));

        return new StorageData(profile, tasks, codingLogs, applications);
    }
}
