package service;

import model.CodingLog;
import model.PlacementApplication;
import model.StudentProfile;
import model.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data container used for saving and loading the complete state.
 */
public class StorageData implements Serializable {
    private static final long serialVersionUID = 1L;

    private StudentProfile profile;
    private List<Task> tasks;
    private List<CodingLog> codingLogs;
    private List<PlacementApplication> applications;

    public StorageData() {
        this.profile = new StudentProfile();
        this.tasks = new ArrayList<>();
        this.codingLogs = new ArrayList<>();
        this.applications = new ArrayList<>();
    }

    public StorageData(StudentProfile profile, List<Task> tasks, List<CodingLog> codingLogs, List<PlacementApplication> applications) {
        this.profile = profile;
        this.tasks = tasks;
        this.codingLogs = codingLogs;
        this.applications = applications;
    }

    public StudentProfile getProfile() { return profile; }
    public void setProfile(StudentProfile profile) { this.profile = profile; }

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }

    public List<CodingLog> getCodingLogs() { return codingLogs; }
    public void setCodingLogs(List<CodingLog> codingLogs) { this.codingLogs = codingLogs; }

    public List<PlacementApplication> getApplications() { return applications; }
    public void setApplications(List<PlacementApplication> applications) { this.applications = applications; }
}
