package ui;

import model.CodingLog;
import model.PlacementApplication;
import model.StudentProfile;
import model.Task;
import service.StorageService;
import service.TrackerService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command-Line Interface for terminal usage.
 */
public class ConsoleView {
    private final TrackerService tracker;
    private final Scanner scanner;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleView(TrackerService tracker) {
        this.tracker = tracker;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader("STUDENT PRODUCTIVITY & PLACEMENT TRACKER");
            System.out.println(" 1. View Dashboard & Readiness Score");
            System.out.println(" 2. Manage Daily Tasks & Productivity");
            System.out.println(" 3. Log / View DSA Coding Practice");
            System.out.println(" 4. Manage Placement Applications");
            System.out.println(" 5. Update Student Profile & Targets");
            System.out.println(" 6. Export Placement Readiness Report (.txt)");
            System.out.println(" 7. Save & Exit");
            System.out.print("\nSelect an option [1-7]: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    showDashboard();
                    break;
                case "2":
                    manageTasks();
                    break;
                case "3":
                    manageCodingLogs();
                    break;
                case "4":
                    managePlacements();
                    break;
                case "5":
                    manageProfile();
                    break;
                case "6":
                    exportReport();
                    break;
                case "7":
                    tracker.save();
                    System.out.println("\nAll changes saved. All the best with your placements!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose 1-7.");
            }
        }
    }

    private void showDashboard() {
        printHeader("EXECUTIVE DASHBOARD");
        StudentProfile p = tracker.getProfile();
        int score = tracker.calculateReadinessScore();

        System.out.printf(" Student: %s (%s) | CGPA: %.2f | Target: %s\n", 
                p.getName(), p.getRollNumber(), p.getCurrentCgpa(), p.getTargetRole());
        System.out.println("-".repeat(70));
        System.out.printf(" PLACEMENT READINESS SCORE : %d / 100\n", score);
        System.out.printf(" Status                    : %s\n", tracker.getReadinessVerdict(score));
        System.out.println("-".repeat(70));
        System.out.printf("  \u2022 CGPA Component         : %.1f / 25 pts\n", tracker.getCgpaScore());
        System.out.printf("  \u2022 DSA Solved Component   : %.1f / 35 pts (%d / %d solved)\n", 
                tracker.getDsaScore(), tracker.getTotalProblemsSolved(), p.getTargetDsaCount());
        System.out.printf("  \u2022 Tasks Completion Rate  : %.1f / 20 pts (%.0f%% done)\n", 
                tracker.getTaskScore(), tracker.getTaskCompletionRate());
        System.out.printf("  \u2022 Applications Pipeline  : %.1f / 20 pts (%d active, %d offers)\n", 
                tracker.getPipelineScore(), tracker.getActiveApplicationsCount(), tracker.getOffersCount());
        System.out.println("-".repeat(70));

        pause();
    }

    private void manageTasks() {
        boolean inTaskMenu = true;
        while (inTaskMenu) {
            printHeader("TASKS & PRODUCTIVITY MANAGEMENT");
            List<Task> tasks = tracker.getTasks();
            if (tasks.isEmpty()) {
                System.out.println("No tasks registered.");
            } else {
                for (int i = 0; i < tasks.size(); i++) {
                    Task t = tasks.get(i);
                    System.out.printf(" %2d. [%s] (%-6s) %-30s | Due: %s | Est: %.1f hrs\n",
                            (i + 1), (t.isCompleted() ? "\u2713" : " "), t.getPriority(), t.getTitle(), t.getDueDate(), t.getEstimatedHours());
                }
            }
            System.out.println("\nOptions: [A]dd Task | [T]oggle Done | [D]elete Task | [B]ack");
            System.out.print("Action: ");
            String opt = scanner.nextLine().trim().toUpperCase();

            switch (opt) {
                case "A":
                    addTaskFlow();
                    break;
                case "T":
                    toggleTaskFlow(tasks);
                    break;
                case "D":
                    deleteTaskFlow(tasks);
                    break;
                case "B":
                    inTaskMenu = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addTaskFlow() {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) return;

        System.out.print("Category (DSA / Core CS / Projects / Aptitude / Resume): ");
        String cat = scanner.nextLine().trim();
        if (cat.isEmpty()) cat = "General";

        System.out.print("Priority (High / Medium / Low): ");
        String priority = scanner.nextLine().trim();
        if (priority.isEmpty()) priority = "Medium";

        System.out.print("Due date (YYYY-MM-DD, press Enter for 3 days from now): ");
        String dStr = scanner.nextLine().trim();
        LocalDate due = LocalDate.now().plusDays(3);
        if (!dStr.isEmpty()) {
            try {
                due = LocalDate.parse(dStr, dtf);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format, defaulted to 3 days from now.");
            }
        }

        System.out.print("Estimated hours: ");
        double hours = 2.0;
        try {
            hours = Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        tracker.addTask(new Task(title, cat, priority, due, hours));
        System.out.println("Task added successfully!");
    }

    private void toggleTaskFlow(List<Task> tasks) {
        if (tasks.isEmpty()) return;
        System.out.print("Enter task number to toggle: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < tasks.size()) {
                tracker.toggleTaskCompletion(tasks.get(idx).getId());
                System.out.println("Task status updated!");
            }
        } catch (Exception e) {
            System.out.println("Invalid number.");
        }
    }

    private void deleteTaskFlow(List<Task> tasks) {
        if (tasks.isEmpty()) return;
        System.out.print("Enter task number to delete: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < tasks.size()) {
                tracker.removeTask(tasks.get(idx).getId());
                System.out.println("Task deleted.");
            }
        } catch (Exception e) {
            System.out.println("Invalid number.");
        }
    }

    private void manageCodingLogs() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("DSA & CODING PRACTICE TRACKER");
            System.out.printf("Total Problems Solved: %d | Total Practice: %.1f hrs\n\n",
                    tracker.getTotalProblemsSolved(), tracker.getTotalPracticeMinutes() / 60.0);

            List<CodingLog> logs = tracker.getCodingLogs();
            if (logs.isEmpty()) {
                System.out.println("No practice sessions logged yet.");
            } else {
                for (int i = 0; i < Math.min(8, logs.size()); i++) {
                    CodingLog l = logs.get(i);
                    System.out.printf(" %2d. %s | %-12s | %-18s | %d probs in %d min (%s)\n",
                            (i + 1), l.getLogDate(), l.getPlatform(), l.getTopic(), l.getProblemsSolved(), l.getDurationMinutes(), l.getDifficulty());
                }
            }

            System.out.println("\nOptions: [L]og Session | [B]ack");
            System.out.print("Action: ");
            String opt = scanner.nextLine().trim().toUpperCase();
            if ("L".equals(opt)) {
                logCodingSessionFlow();
            } else if ("B".equals(opt)) {
                inMenu = false;
            }
        }
    }

    private void logCodingSessionFlow() {
        System.out.print("Platform (LeetCode / Codeforces / HackerRank / GFG): ");
        String platform = scanner.nextLine().trim();
        if (platform.isEmpty()) platform = "LeetCode";

        System.out.print("Topic (e.g., Arrays, DP, Graphs, Binary Search): ");
        String topic = scanner.nextLine().trim();
        if (topic.isEmpty()) topic = "General DSA";

        System.out.print("Difficulty (Easy / Medium / Hard): ");
        String diff = scanner.nextLine().trim();
        if (diff.isEmpty()) diff = "Medium";

        System.out.print("Number of problems solved: ");
        int count = 1;
        try {
            count = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        System.out.print("Duration in minutes: ");
        int mins = 60;
        try {
            mins = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        System.out.print("Notes / Key Takeaways: ");
        String notes = scanner.nextLine().trim();

        tracker.addCodingLog(new CodingLog(platform, topic, diff, count, mins, LocalDate.now(), notes));
        System.out.println("Coding practice session recorded!");
    }

    private void managePlacements() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("PLACEMENT APPLICATIONS TRACKER");
            List<PlacementApplication> apps = tracker.getApplications();
            if (apps.isEmpty()) {
                System.out.println("No placement applications registered.");
            } else {
                for (int i = 0; i < apps.size(); i++) {
                    PlacementApplication a = apps.get(i);
                    System.out.printf(" %2d. %-18s | %-20s | %4.1f LPA | %s\n",
                            (i + 1), a.getCompanyName(), a.getRole(), a.getCtcLpa(), a.getStatus().getDisplayName());
                }
            }

            System.out.println("\nOptions: [A]dd Application | [U]pdate Status | [D]elete | [B]ack");
            System.out.print("Action: ");
            String opt = scanner.nextLine().trim().toUpperCase();

            switch (opt) {
                case "A":
                    addApplicationFlow();
                    break;
                case "U":
                    updateStatusFlow(apps);
                    break;
                case "D":
                    deleteApplicationFlow(apps);
                    break;
                case "B":
                    inMenu = false;
                    break;
            }
        }
    }

    private void addApplicationFlow() {
        System.out.print("Company Name: ");
        String company = scanner.nextLine().trim();
        if (company.isEmpty()) return;

        System.out.print("Role: ");
        String role = scanner.nextLine().trim();

        System.out.print("CTC (in LPA, e.g. 18.5): ");
        double ctc = 0.0;
        try {
            ctc = Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception ignored) {}

        System.out.print("Next round date (YYYY-MM-DD, or leave blank): ");
        String dStr = scanner.nextLine().trim();
        LocalDate nextDate = null;
        if (!dStr.isEmpty()) {
            try {
                nextDate = LocalDate.parse(dStr, dtf);
            } catch (Exception ignored) {}
        }

        System.out.print("Notes / Round details: ");
        String notes = scanner.nextLine().trim();

        tracker.addApplication(new PlacementApplication(
                company, role, ctc, PlacementApplication.Status.APPLIED,
                LocalDate.now(), nextDate, "Initial Application", notes
        ));
        System.out.println("Application added successfully!");
    }

    private void updateStatusFlow(List<PlacementApplication> apps) {
        if (apps.isEmpty()) return;
        System.out.print("Select application number: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < apps.size()) {
                PlacementApplication a = apps.get(idx);
                System.out.println("Available statuses:");
                PlacementApplication.Status[] statuses = PlacementApplication.Status.values();
                for (int i = 0; i < statuses.length; i++) {
                    System.out.printf("  %d. %s\n", (i + 1), statuses[i].getDisplayName());
                }
                System.out.print("Choose status number: ");
                int sIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (sIdx >= 0 && sIdx < statuses.length) {
                    tracker.updateApplicationStatus(a.getId(), statuses[sIdx]);
                    System.out.println("Status updated to: " + statuses[sIdx].getDisplayName());
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid selection.");
        }
    }

    private void deleteApplicationFlow(List<PlacementApplication> apps) {
        if (apps.isEmpty()) return;
        System.out.print("Select application number to delete: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx >= 0 && idx < apps.size()) {
                tracker.removeApplication(apps.get(idx).getId());
                System.out.println("Application removed.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void manageProfile() {
        printHeader("STUDENT PROFILE & GOALS");
        StudentProfile p = tracker.getProfile();
        System.out.printf("Current Name     : %s\n", p.getName());
        System.out.printf("Roll Number      : %s\n", p.getRollNumber());
        System.out.printf("Department       : %s\n", p.getDepartment());
        System.out.printf("CGPA             : %.2f\n", p.getCurrentCgpa());
        System.out.printf("Target Role      : %s\n", p.getTargetRole());
        System.out.printf("DSA Target Goal  : %d solved\n", p.getTargetDsaCount());
        System.out.printf("Weekly Study Goal: %d hours\n", p.getTargetWeeklyStudyHours());

        System.out.print("\nDo you want to update profile details? (y/n): ");
        if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.print("New Name (leave blank to keep current): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) p.setName(name);

            System.out.print("New CGPA (leave blank to keep current): ");
            String cgpaStr = scanner.nextLine().trim();
            if (!cgpaStr.isEmpty()) {
                try { p.setCurrentCgpa(Double.parseDouble(cgpaStr)); } catch (Exception ignored) {}
            }

            System.out.print("New Target Role (leave blank to keep current): ");
            String role = scanner.nextLine().trim();
            if (!role.isEmpty()) p.setTargetRole(role);

            System.out.print("New Target DSA Count (leave blank to keep current): ");
            String dsaStr = scanner.nextLine().trim();
            if (!dsaStr.isEmpty()) {
                try { p.setTargetDsaCount(Integer.parseInt(dsaStr)); } catch (Exception ignored) {}
            }

            tracker.updateProfile(p);
            System.out.println("Profile updated successfully!");
        }
    }

    private void exportReport() {
        try {
            File report = StorageService.exportSummaryReport(tracker, "Placement_Readiness_Report.txt");
            System.out.println("\nReport exported successfully to: " + report.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
        }
        pause();
    }

    private void printHeader(String title) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  " + title);
        System.out.println("=".repeat(70));
    }

    private void pause() {
        System.out.print("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }
}
