package ui;

import model.CodingLog;
import model.PlacementApplication;
import model.StudentProfile;
import model.Task;
import service.StorageService;
import service.TrackerService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Modern Java Swing Graphical Interface for Student Productivity & Placement Tracker.
 */
public class MainFrame extends JFrame {
    private final TrackerService tracker;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Header labels
    private JLabel headerStudentLabel;
    private JLabel headerScoreBadge;

    // Dashboard widgets
    private JLabel readinessPercentLabel;
    private JProgressBar readinessProgressBar;
    private JLabel readinessVerdictLabel;
    private JLabel kpiDsaSolvedLabel;
    private JProgressBar dsaProgressBar;
    private JLabel kpiTasksPendingLabel;
    private JLabel kpiTasksRateLabel;
    private JLabel kpiApplicationsActiveLabel;
    private JLabel kpiOffersLabel;
    private JTextArea recommendationsArea;

    // Tasks table
    private DefaultTableModel tasksTableModel;
    private JTable tasksTable;
    private JComboBox<String> taskFilterCombo;

    // DSA table
    private DefaultTableModel dsaTableModel;
    private JTable dsaTable;
    private JLabel dsaStatsSummaryLabel;

    // Placement table
    private DefaultTableModel placementTableModel;
    private JTable placementTable;

    // Profile fields
    private JTextField profileNameField;
    private JTextField profileRollField;
    private JTextField profileDeptField;
    private JTextField profileCgpaField;
    private JTextField profileRoleField;
    private JTextField profileDsaTargetField;
    private JTextField profileStudyHoursField;

    public MainFrame(TrackerService tracker) {
        this.tracker = tracker;

        setTitle("Student Productivity & Placement Tracker - Pure Java");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set modern Look & Feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        initComponents();

        // Subscribe to data changes
        tracker.addChangeListener(this::refreshAllViews);

        refreshAllViews();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(10, 15, 15, 15));
        root.setBackground(new Color(245, 247, 250));

        // Header
        root.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("  \uD83D\uDCCA Executive Dashboard  ", createDashboardTab());
        tabbedPane.addTab("  \u2705 Tasks & Productivity  ", createTasksTab());
        tabbedPane.addTab("  \uD83D\uDCBB DSA & Coding Practice  ", createDsaTab());
        tabbedPane.addTab("  \uD83C\uDFC6 Placement Applications  ", createPlacementTab());
        tabbedPane.addTab("  \uD83D\uDC64 Student Profile & Export  ", createProfileTab());

        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 5, 10, 5));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);

        JLabel title = new JLabel("STUDENT PRODUCTIVITY & PLACEMENT TRACKER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 41, 59));

        headerStudentLabel = new JLabel("Loading student profile...");
        headerStudentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        headerStudentLabel.setForeground(new Color(100, 116, 139));

        left.add(title);
        left.add(headerStudentLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        right.setOpaque(false);

        headerScoreBadge = new JLabel("Score: --%");
        headerScoreBadge.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerScoreBadge.setOpaque(true);
        headerScoreBadge.setBackground(new Color(16, 185, 129));
        headerScoreBadge.setForeground(Color.WHITE);
        headerScoreBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(5, 150, 105), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        right.add(headerScoreBadge);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    // =========================================================================
    // TAB 1: EXECUTIVE DASHBOARD
    // =========================================================================
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Metric Cards Grid
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsGrid.setOpaque(false);

        // Card 1: Readiness Score
        JPanel card1 = createCard("Placement Readiness", new Color(79, 70, 229));
        readinessPercentLabel = new JLabel("0%", SwingConstants.CENTER);
        readinessPercentLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        readinessPercentLabel.setForeground(new Color(79, 70, 229));

        readinessProgressBar = new JProgressBar(0, 100);
        readinessProgressBar.setStringPainted(true);
        readinessProgressBar.setPreferredSize(new Dimension(150, 18));
        readinessProgressBar.setForeground(new Color(79, 70, 229));

        readinessVerdictLabel = new JLabel("Evaluating...", SwingConstants.CENTER);
        readinessVerdictLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        card1.add(readinessPercentLabel);
        card1.add(Box.createVerticalStrut(5));
        card1.add(readinessProgressBar);
        card1.add(Box.createVerticalStrut(5));
        card1.add(readinessVerdictLabel);

        // Card 2: DSA Progress
        JPanel card2 = createCard("DSA Coding Milestone", new Color(14, 165, 233));
        kpiDsaSolvedLabel = new JLabel("0 Problems", SwingConstants.CENTER);
        kpiDsaSolvedLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        kpiDsaSolvedLabel.setForeground(new Color(14, 165, 233));

        dsaProgressBar = new JProgressBar(0, 200);
        dsaProgressBar.setStringPainted(true);
        dsaProgressBar.setPreferredSize(new Dimension(150, 18));
        dsaProgressBar.setForeground(new Color(14, 165, 233));

        card2.add(kpiDsaSolvedLabel);
        card2.add(Box.createVerticalStrut(8));
        card2.add(dsaProgressBar);

        // Card 3: Tasks & Execution
        JPanel card3 = createCard("Productivity & Tasks", new Color(245, 158, 11));
        kpiTasksPendingLabel = new JLabel("0 Pending", SwingConstants.CENTER);
        kpiTasksPendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        kpiTasksPendingLabel.setForeground(new Color(217, 119, 6));

        kpiTasksRateLabel = new JLabel("Completion Rate: 0%", SwingConstants.CENTER);
        kpiTasksRateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card3.add(kpiTasksPendingLabel);
        card3.add(Box.createVerticalStrut(8));
        card3.add(kpiTasksRateLabel);

        // Card 4: Placement Drives
        JPanel card4 = createCard("Applications & Offers", new Color(16, 185, 129));
        kpiApplicationsActiveLabel = new JLabel("0 Active Drives", SwingConstants.CENTER);
        kpiApplicationsActiveLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        kpiApplicationsActiveLabel.setForeground(new Color(16, 185, 129));

        kpiOffersLabel = new JLabel("\uD83C\uDF89 0 Offers Secured", SwingConstants.CENTER);
        kpiOffersLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        kpiOffersLabel.setForeground(new Color(5, 150, 105));

        card4.add(kpiApplicationsActiveLabel);
        card4.add(Box.createVerticalStrut(8));
        card4.add(kpiOffersLabel);

        cardsGrid.add(card1);
        cardsGrid.add(card2);
        cardsGrid.add(card3);
        cardsGrid.add(card4);

        panel.add(cardsGrid, BorderLayout.NORTH);

        // Bottom area: Recommendations & Quick Status
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        JLabel recTitle = new JLabel("\uD83D\uDCA1 Intelligent Readiness Recommendations & Analysis");
        recTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        recTitle.setForeground(new Color(51, 65, 85));

        recommendationsArea = new JTextArea();
        recommendationsArea.setEditable(false);
        recommendationsArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        recommendationsArea.setBackground(new Color(248, 250, 252));
        recommendationsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JScrollPane scrollPane = new JScrollPane(recommendationsArea);
        scrollPane.setBorder(null);

        centerPanel.add(recTitle, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        BorderFactory.createEmptyBorder(12, 12, 12, 12)
                )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(71, 85, 105));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        return card;
    }

    // =========================================================================
    // TAB 2: TASKS & PRODUCTIVITY
    // =========================================================================
    private JPanel createTasksTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton addTaskBtn = new JButton("+ Add Task");
        addTaskBtn.setBackground(new Color(79, 70, 229));
        addTaskBtn.setForeground(Color.WHITE);
        addTaskBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addTaskBtn.setFocusPainted(false);
        addTaskBtn.addActionListener(e -> showAddTaskDialog());

        JButton toggleBtn = new JButton("\u2713 Mark Complete / Incomplete");
        toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toggleBtn.addActionListener(e -> toggleSelectedTask());

        JButton deleteBtn = new JButton("\uD83D\uDDD1 Delete Task");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setForeground(new Color(220, 38, 38));
        deleteBtn.addActionListener(e -> deleteSelectedTask());

        JLabel filterLabel = new JLabel("  Filter:");
        taskFilterCombo = new JComboBox<>(new String[]{"All Tasks", "Pending Only", "Completed Only"});
        taskFilterCombo.addActionListener(e -> refreshTasksTable());

        toolbar.add(addTaskBtn);
        toolbar.add(toggleBtn);
        toolbar.add(deleteBtn);
        toolbar.add(filterLabel);
        toolbar.add(taskFilterCombo);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Status", "Priority", "Task Title", "Category", "Due Date", "Est. Hours", "ID"};
        tasksTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tasksTable = new JTable(tasksTableModel);
        tasksTable.setRowHeight(28);
        tasksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tasksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Hide ID column
        tasksTable.removeColumn(tasksTable.getColumnModel().getColumn(6));

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void showAddTaskDialog() {
        JDialog dialog = new JDialog(this, "Add New Productivity Task", true);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 12));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField titleField = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"DSA", "Core CS", "Projects", "Aptitude", "Resume/Prep", "General"});
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        JTextField dueDaysField = new JTextField("3");
        JTextField hoursField = new JTextField("2.0");

        form.add(new JLabel("Task Title:"));
        form.add(titleField);
        form.add(new JLabel("Category:"));
        form.add(catBox);
        form.add(new JLabel("Priority:"));
        form.add(priorityBox);
        form.add(new JLabel("Due in (days):"));
        form.add(dueDaysField);
        form.add(new JLabel("Estimated Hours:"));
        form.add(hoursField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Add Task");
        saveBtn.setBackground(new Color(79, 70, 229));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a task title.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int days = 3;
            double hours = 2.0;
            try { days = Integer.parseInt(dueDaysField.getText().trim()); } catch (Exception ignored) {}
            try { hours = Double.parseDouble(hoursField.getText().trim()); } catch (Exception ignored) {}

            Task task = new Task(title, (String) catBox.getSelectedItem(), (String) priorityBox.getSelectedItem(),
                    LocalDate.now().plusDays(days), hours);
            tracker.addTask(task);
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void toggleSelectedTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String taskId = (String) tasksTableModel.getValueAt(row, 6);
        tracker.toggleTaskCompletion(taskId);
    }

    private void deleteSelectedTask() {
        int row = tasksTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String taskId = (String) tasksTableModel.getValueAt(row, 6);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.removeTask(taskId);
        }
    }

    // =========================================================================
    // TAB 3: DSA & SKILL PRACTICE
    // =========================================================================
    private JPanel createDsaTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setOpaque(false);

        JButton addLogBtn = new JButton("+ Log Practice Session");
        addLogBtn.setBackground(new Color(14, 165, 233));
        addLogBtn.setForeground(Color.WHITE);
        addLogBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addLogBtn.setFocusPainted(false);
        addLogBtn.addActionListener(e -> showAddCodingLogDialog());

        JButton deleteLogBtn = new JButton("\uD83D\uDDD1 Delete Entry");
        deleteLogBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteLogBtn.setForeground(new Color(220, 38, 38));
        deleteLogBtn.addActionListener(e -> deleteSelectedLog());

        leftButtons.add(addLogBtn);
        leftButtons.add(deleteLogBtn);

        dsaStatsSummaryLabel = new JLabel("Total: 0 problems | 0.0 hrs  ");
        dsaStatsSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dsaStatsSummaryLabel.setForeground(new Color(14, 165, 233));

        toolbar.add(leftButtons, BorderLayout.WEST);
        toolbar.add(dsaStatsSummaryLabel, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Date", "Platform", "Topic / Category", "Difficulty", "Solved", "Time (Mins)", "Notes / Takeaways", "ID"};
        dsaTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        dsaTable = new JTable(dsaTableModel);
        dsaTable.setRowHeight(28);
        dsaTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dsaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        dsaTable.removeColumn(dsaTable.getColumnModel().getColumn(7)); // Hide ID

        JScrollPane scrollPane = new JScrollPane(dsaTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void showAddCodingLogDialog() {
        JDialog dialog = new JDialog(this, "Log DSA Practice Session", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 12));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        JComboBox<String> platformBox = new JComboBox<>(new String[]{"LeetCode", "Codeforces", "HackerRank", "GeeksforGeeks", "CodeStudio", "InterviewBit"});
        JTextField topicField = new JTextField("Dynamic Programming");
        JComboBox<String> diffBox = new JComboBox<>(new String[]{"Medium", "Easy", "Hard"});
        JTextField solvedField = new JTextField("3");
        JTextField minsField = new JTextField("60");
        JTextField notesField = new JTextField("Patterns & takeaways");

        form.add(new JLabel("Platform:"));
        form.add(platformBox);
        form.add(new JLabel("Topic / Algorithm:"));
        form.add(topicField);
        form.add(new JLabel("Difficulty:"));
        form.add(diffBox);
        form.add(new JLabel("Problems Solved:"));
        form.add(solvedField);
        form.add(new JLabel("Time Spent (Minutes):"));
        form.add(minsField);
        form.add(new JLabel("Notes:"));
        form.add(notesField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save Log");
        saveBtn.setBackground(new Color(14, 165, 233));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            int solved = 1;
            int mins = 60;
            try { solved = Integer.parseInt(solvedField.getText().trim()); } catch (Exception ignored) {}
            try { mins = Integer.parseInt(minsField.getText().trim()); } catch (Exception ignored) {}

            CodingLog log = new CodingLog((String) platformBox.getSelectedItem(),
                    topicField.getText().trim(),
                    (String) diffBox.getSelectedItem(),
                    solved, mins, LocalDate.now(),
                    notesField.getText().trim());
            tracker.addCodingLog(log);
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedLog() {
        int row = dsaTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) dsaTableModel.getValueAt(row, 7);
        tracker.removeCodingLog(id);
    }

    // =========================================================================
    // TAB 4: PLACEMENT APPLICATIONS
    // =========================================================================
    private JPanel createPlacementTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton addAppBtn = new JButton("+ Add Placement Drive");
        addAppBtn.setBackground(new Color(16, 185, 129));
        addAppBtn.setForeground(Color.WHITE);
        addAppBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addAppBtn.setFocusPainted(false);
        addAppBtn.addActionListener(e -> showAddApplicationDialog());

        JButton updateStatusBtn = new JButton("\u2699 Update Stage / Status");
        updateStatusBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        updateStatusBtn.addActionListener(e -> updateSelectedAppStatus());

        JButton deleteAppBtn = new JButton("\uD83D\uDDD1 Remove Drive");
        deleteAppBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteAppBtn.setForeground(new Color(220, 38, 38));
        deleteAppBtn.addActionListener(e -> deleteSelectedApplication());

        toolbar.add(addAppBtn);
        toolbar.add(updateStatusBtn);
        toolbar.add(deleteAppBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Company", "Role", "CTC (LPA)", "Stage / Status", "Applied Date", "Next Round Date", "Round Details", "ID"};
        placementTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        placementTable = new JTable(placementTableModel);
        placementTable.setRowHeight(28);
        placementTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        placementTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        placementTable.removeColumn(placementTable.getColumnModel().getColumn(7)); // Hide ID

        JScrollPane scrollPane = new JScrollPane(placementTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void showAddApplicationDialog() {
        JDialog dialog = new JDialog(this, "Add Placement Drive / Application", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 12));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField companyField = new JTextField();
        JTextField roleField = new JTextField("Software Development Engineer");
        JTextField ctcField = new JTextField("18.0");
        JComboBox<PlacementApplication.Status> statusBox = new JComboBox<>(PlacementApplication.Status.values());
        JTextField nextDaysField = new JTextField("5");
        JTextField notesField = new JTextField("Applied on campus portal");

        form.add(new JLabel("Company Name:"));
        form.add(companyField);
        form.add(new JLabel("Role:"));
        form.add(roleField);
        form.add(new JLabel("CTC (LPA):"));
        form.add(ctcField);
        form.add(new JLabel("Current Stage:"));
        form.add(statusBox);
        form.add(new JLabel("Next Round in (days):"));
        form.add(nextDaysField);
        form.add(new JLabel("Round Details / Notes:"));
        form.add(notesField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Add Application");
        saveBtn.setBackground(new Color(16, 185, 129));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String company = companyField.getText().trim();
            if (company.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter company name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double ctc = 0.0;
            try { ctc = Double.parseDouble(ctcField.getText().trim()); } catch (Exception ignored) {}

            int nextDays = 5;
            try { nextDays = Integer.parseInt(nextDaysField.getText().trim()); } catch (Exception ignored) {}
            LocalDate nextDate = (nextDays > 0) ? LocalDate.now().plusDays(nextDays) : null;

            PlacementApplication app = new PlacementApplication(
                    company,
                    roleField.getText().trim(),
                    ctc,
                    (PlacementApplication.Status) statusBox.getSelectedItem(),
                    LocalDate.now(),
                    nextDate,
                    notesField.getText().trim(),
                    ""
            );
            tracker.addApplication(app);
            dialog.dispose();
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateSelectedAppStatus() {
        int row = placementTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an application from the table.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) placementTableModel.getValueAt(row, 7);

        PlacementApplication.Status selectedStatus = (PlacementApplication.Status) JOptionPane.showInputDialog(
                this,
                "Select new stage/status for this application:",
                "Update Placement Stage",
                JOptionPane.QUESTION_MESSAGE,
                null,
                PlacementApplication.Status.values(),
                PlacementApplication.Status.values()[0]
        );

        if (selectedStatus != null) {
            tracker.updateApplicationStatus(id, selectedStatus);
        }
    }

    private void deleteSelectedApplication() {
        int row = placementTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an application to remove.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String id = (String) placementTableModel.getValueAt(row, 7);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this application?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tracker.removeApplication(id);
        }
    }

    // =========================================================================
    // TAB 5: STUDENT PROFILE & EXPORT
    // =========================================================================
    private JPanel createProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 15));
        form.setOpaque(false);

        profileNameField = new JTextField();
        profileRollField = new JTextField();
        profileDeptField = new JTextField();
        profileCgpaField = new JTextField();
        profileRoleField = new JTextField();
        profileDsaTargetField = new JTextField();
        profileStudyHoursField = new JTextField();

        form.add(new JLabel("Full Name:"));
        form.add(profileNameField);
        form.add(new JLabel("Roll / Registration Number:"));
        form.add(profileRollField);
        form.add(new JLabel("Department / Branch:"));
        form.add(profileDeptField);
        form.add(new JLabel("Current CGPA (out of 10.0):"));
        form.add(profileCgpaField);
        form.add(new JLabel("Target Role / Dream Job:"));
        form.add(profileRoleField);
        form.add(new JLabel("Target DSA Problems Milestone:"));
        form.add(profileDsaTargetField);
        form.add(new JLabel("Target Weekly Study Hours:"));
        form.add(profileStudyHoursField);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonBar.setOpaque(false);

        JButton saveProfileBtn = new JButton("\uD83D\uDCBE Save Profile Changes");
        saveProfileBtn.setBackground(new Color(79, 70, 229));
        saveProfileBtn.setForeground(Color.WHITE);
        saveProfileBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveProfileBtn.addActionListener(e -> saveProfileChanges());

        JButton exportBtn = new JButton("\uD83D\uDCC4 Export Placement Report (.txt)");
        exportBtn.setBackground(new Color(16, 185, 129));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        exportBtn.addActionListener(e -> exportReportFile());

        buttonBar.add(saveProfileBtn);
        buttonBar.add(exportBtn);

        panel.add(form, BorderLayout.NORTH);
        panel.add(buttonBar, BorderLayout.CENTER);
        return panel;
    }

    private void saveProfileChanges() {
        try {
            StudentProfile p = tracker.getProfile();
            p.setName(profileNameField.getText().trim());
            p.setRollNumber(profileRollField.getText().trim());
            p.setDepartment(profileDeptField.getText().trim());
            p.setCurrentCgpa(Double.parseDouble(profileCgpaField.getText().trim()));
            p.setTargetRole(profileRoleField.getText().trim());
            p.setTargetDsaCount(Integer.parseInt(profileDsaTargetField.getText().trim()));
            p.setTargetWeeklyStudyHours(Integer.parseInt(profileStudyHoursField.getText().trim()));

            tracker.updateProfile(p);
            JOptionPane.showMessageDialog(this, "Profile and placement targets updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for CGPA, DSA target, and Study Hours.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportReportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Placement_Readiness_Report.txt"));
        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                StorageService.exportSummaryReport(tracker, file.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Placement report saved successfully at:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // VIEW REFRESH & STATE SYNCHRONIZATION
    // =========================================================================
    private void refreshAllViews() {
        StudentProfile p = tracker.getProfile();
        int score = tracker.calculateReadinessScore();

        // 1. Header
        headerStudentLabel.setText(String.format("Student: %s (%s)  |  CGPA: %.2f  |  Target: %s",
                p.getName(), p.getRollNumber(), p.getCurrentCgpa(), p.getTargetRole()));
        headerScoreBadge.setText(String.format("Readiness: %d%%", score));

        // 2. Dashboard Cards
        readinessPercentLabel.setText(score + "%");
        readinessProgressBar.setValue(score);
        readinessVerdictLabel.setText(tracker.getReadinessVerdict(score));

        int solved = tracker.getTotalProblemsSolved();
        int dsaTarget = p.getTargetDsaCount();
        kpiDsaSolvedLabel.setText(solved + " / " + dsaTarget);
        dsaProgressBar.setMaximum(dsaTarget);
        dsaProgressBar.setValue(solved);

        long pendingTasks = tracker.getPendingTasks().size();
        kpiTasksPendingLabel.setText(pendingTasks + " Pending");
        kpiTasksRateLabel.setText(String.format("Completion Rate: %.0f%%", tracker.getTaskCompletionRate()));

        kpiApplicationsActiveLabel.setText(tracker.getActiveApplicationsCount() + " Active Drives");
        kpiOffersLabel.setText(String.format("\uD83C\uDF89 %d Offer(s) Secured", tracker.getOffersCount()));

        // Recommendations Text
        StringBuilder rec = new StringBuilder();
        rec.append("== READINESS BENCHMARK SCORE BREAKDOWN ==\n");
        rec.append(String.format(" \u2022 Academic CGPA Score        : %.1f / 25 pts (Current CGPA: %.2f)\n", tracker.getCgpaScore(), p.getCurrentCgpa()));
        rec.append(String.format(" \u2022 Coding / DSA Mastery       : %.1f / 35 pts (%d of %d target solved)\n", tracker.getDsaScore(), solved, dsaTarget));
        rec.append(String.format(" \u2022 Task & Preparation Pace    : %.1f / 20 pts (%.1f%% complete)\n", tracker.getTaskScore(), tracker.getTaskCompletionRate()));
        rec.append(String.format(" \u2022 Placement Pipeline & Offers: %.1f / 20 pts (%d active, %d offers)\n\n", tracker.getPipelineScore(), tracker.getActiveApplicationsCount(), tracker.getOffersCount()));

        rec.append("== STRATEGIC ACTION ITEMS ==\n");
        if (p.getCurrentCgpa() < 8.0) {
            rec.append(" \u25B6 Academics: Aim to raise CGPA above 8.0 to qualify for strict shortlisting filters.\n");
        } else {
            rec.append(" \u2705 Academics: CGPA is in healthy standing for top tier companies.\n");
        }
        if (solved < dsaTarget) {
            rec.append(String.format(" \u25B6 Coding: Solve %d more problems to hit your milestone of %d.\n", (dsaTarget - solved), dsaTarget));
        } else {
            rec.append(" \u2705 Coding: DSA milestone achieved! Keep participating in weekly contests.\n");
        }
        if (pendingTasks > 0) {
            rec.append(String.format(" \u25B6 Productivity: You have %d pending tasks due soon. Prioritize High priority items.\n", pendingTasks));
        }
        if (tracker.getActiveApplicationsCount() < 3 && tracker.getOffersCount() == 0) {
            rec.append(" \u25B6 Applications: Pipeline has fewer than 3 active drives. Apply to more companies to improve odds!\n");
        }

        rec.append("\n== DSA TOPIC DISTRIBUTION ==\n");
        Map<String, Integer> topics = tracker.getProblemsByTopic();
        if (topics.isEmpty()) {
            rec.append(" (No topics recorded yet)\n");
        } else {
            topics.forEach((t, c) -> rec.append(String.format("  - %-22s: %d problems\n", t, c)));
        }

        recommendationsArea.setText(rec.toString());
        recommendationsArea.setCaretPosition(0);

        // 3. Tables
        refreshTasksTable();
        refreshDsaTable();
        refreshPlacementTable();

        // 4. Profile inputs
        profileNameField.setText(p.getName());
        profileRollField.setText(p.getRollNumber());
        profileDeptField.setText(p.getDepartment());
        profileCgpaField.setText(String.valueOf(p.getCurrentCgpa()));
        profileRoleField.setText(p.getTargetRole());
        profileDsaTargetField.setText(String.valueOf(p.getTargetDsaCount()));
        profileStudyHoursField.setText(String.valueOf(p.getTargetWeeklyStudyHours()));
    }

    private void refreshTasksTable() {
        tasksTableModel.setRowCount(0);
        String filter = (taskFilterCombo != null) ? (String) taskFilterCombo.getSelectedItem() : "All Tasks";
        List<Task> tasks = tracker.getTasks();

        for (Task t : tasks) {
            if ("Pending Only".equals(filter) && t.isCompleted()) continue;
            if ("Completed Only".equals(filter) && !t.isCompleted()) continue;

            tasksTableModel.addRow(new Object[]{
                    t.isCompleted() ? "\u2705 Done" : "\u23F3 Pending",
                    t.getPriority(),
                    t.getTitle(),
                    t.getCategory(),
                    t.getDueDate().format(dtf),
                    t.getEstimatedHours() + " hrs",
                    t.getId()
            });
        }
    }

    private void refreshDsaTable() {
        dsaTableModel.setRowCount(0);
        for (CodingLog log : tracker.getCodingLogs()) {
            dsaTableModel.addRow(new Object[]{
                    log.getLogDate().format(dtf),
                    log.getPlatform(),
                    log.getTopic(),
                    log.getDifficulty(),
                    log.getProblemsSolved(),
                    log.getDurationMinutes(),
                    log.getNotes(),
                    log.getId()
            });
        }
        dsaStatsSummaryLabel.setText(String.format("Total: %d problems | %.1f hrs practice  ",
                tracker.getTotalProblemsSolved(), tracker.getTotalPracticeMinutes() / 60.0));
    }

    private void refreshPlacementTable() {
        placementTableModel.setRowCount(0);
        for (PlacementApplication app : tracker.getApplications()) {
            String nextDate = (app.getNextRoundDate() != null) ? app.getNextRoundDate().format(dtf) : "TBD";
            placementTableModel.addRow(new Object[]{
                    app.getCompanyName(),
                    app.getRole(),
                    app.getCtcLpa() + " LPA",
                    app.getStatus().getDisplayName(),
                    app.getAppliedDate().format(dtf),
                    nextDate,
                    app.getRoundDetails(),
                    app.getId()
            });
        }
    }
}
