package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a company placement drive or job application.
 */
public class PlacementApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        WISHLIST("Wishlist"),
        APPLIED("Applied"),
        OA_SCHEDULED("Online Assessment"),
        OA_CLEARED("OA Cleared"),
        TECH_INTERVIEW("Tech Interview"),
        HR_ROUND("HR Round"),
        OFFER_RECEIVED("Offer Received! \u2705"),
        REJECTED("Rejected");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public static Status fromString(String text) {
            for (Status s : Status.values()) {
                if (s.name().equalsIgnoreCase(text) || s.displayName.equalsIgnoreCase(text)) {
                    return s;
                }
            }
            return APPLIED;
        }
    }

    private String id;
    private String companyName;
    private String role;
    private double ctcLpa;
    private Status status;
    private LocalDate appliedDate;
    private LocalDate nextRoundDate;
    private String roundDetails;
    private String notes;

    public PlacementApplication(String companyName, String role, double ctcLpa, Status status,
                                LocalDate appliedDate, LocalDate nextRoundDate, String roundDetails, String notes) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.companyName = companyName;
        this.role = role;
        this.ctcLpa = ctcLpa;
        this.status = status;
        this.appliedDate = appliedDate;
        this.nextRoundDate = nextRoundDate;
        this.roundDetails = roundDetails;
        this.notes = notes;
    }

    public String getId() { return id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getCtcLpa() { return ctcLpa; }
    public void setCtcLpa(double ctcLpa) { this.ctcLpa = ctcLpa; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public LocalDate getNextRoundDate() { return nextRoundDate; }
    public void setNextRoundDate(LocalDate nextRoundDate) { this.nextRoundDate = nextRoundDate; }

    public String getRoundDetails() { return roundDetails; }
    public void setRoundDetails(String roundDetails) { this.roundDetails = roundDetails; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return companyName + " - " + role + " (" + ctcLpa + " LPA) [" + status.getDisplayName() + "]";
    }
}
