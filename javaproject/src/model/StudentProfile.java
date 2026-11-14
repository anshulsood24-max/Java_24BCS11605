package model;

import java.io.Serializable;

/**
 * Represents a student's academic and placement profile.
 */
public class StudentProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String rollNumber;
    private String department;
    private double currentCgpa;
    private String targetRole;
    private int targetDsaCount;
    private int targetWeeklyStudyHours;

    public StudentProfile() {
        this.name = "Student Name";
        this.rollNumber = "CS2026-001";
        this.department = "Computer Science & Engineering";
        this.currentCgpa = 8.5;
        this.targetRole = "Software Development Engineer (SDE)";
        this.targetDsaCount = 200;
        this.targetWeeklyStudyHours = 25;
    }

    public StudentProfile(String name, String rollNumber, String department, double currentCgpa,
                          String targetRole, int targetDsaCount, int targetWeeklyStudyHours) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.department = department;
        this.currentCgpa = currentCgpa;
        this.targetRole = targetRole;
        this.targetDsaCount = targetDsaCount;
        this.targetWeeklyStudyHours = targetWeeklyStudyHours;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public double getCurrentCgpa() { return currentCgpa; }
    public void setCurrentCgpa(double currentCgpa) { this.currentCgpa = currentCgpa; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getTargetDsaCount() { return targetDsaCount; }
    public void setTargetDsaCount(int targetDsaCount) { this.targetDsaCount = targetDsaCount; }

    public int getTargetWeeklyStudyHours() { return targetWeeklyStudyHours; }
    public void setTargetWeeklyStudyHours(int targetWeeklyStudyHours) { this.targetWeeklyStudyHours = targetWeeklyStudyHours; }
}
