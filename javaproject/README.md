# Student Productivity & Placement Tracker

A 100% **Pure Java** application designed to help college and university students systematically track their daily study productivity, DSA coding progress, and placement drives, while calculating an algorithmic **Placement Readiness Score (0–100%)**.

Built with **Zero External Dependencies** — uses only the standard Java runtime environment (`java.desktop`, `java.time`, `java.io`, `java.util`).

---

## 🌟 Key Features

### 1. 📊 Executive Dashboard & Readiness Engine
- **Algorithmic Placement Readiness Score (0–100%)**:
  - **Academic Standing (25%)**: Dynamic scoring based on CGPA.
  - **DSA / Coding Mastery (35%)**: Progress against target coding milestones.
  - **Task & Time Execution (20%)**: Productivity completion rate.
  - **Placement Pipeline (20%)**: Active company drives and offers secured.
- Real-time progress bars, status badges, and actionable recommendations.
- DSA Topic distribution breakdown.

### 2. 📅 Tasks & Productivity Manager
- Manage prep tasks by category (**DSA**, **Core CS**, **Projects**, **Aptitude**, **Resume/HR**).
- Priority badges (**High**, **Medium**, **Low**) and due date reminders.
- Estimated hours tracking and instant completion toggle.
- Filter by *All Tasks*, *Pending Only*, or *Completed Only*.

### 3. 💻 DSA & Coding Practice Tracker
- Log practice sessions across platforms: **LeetCode**, **Codeforces**, **HackerRank**, **GeeksforGeeks**, etc.
- Track problem count, difficulty (**Easy**, **Medium**, **Hard**), and time spent.
- Maintain notes, algorithmic patterns, and key takeaways for quick interview revision.

### 4. 🏆 Placement Drives & Job Applications
- Track recruitment pipelines from **Wishlist** → **Applied** → **Online Assessment** → **Tech Interview** → **HR Round** → **Offer Received!**
- Log company name, job role, CTC (in LPA), upcoming round dates, and round notes.

### 5. 👤 Profile & One-Click Report Export
- Configure target role, target DSA problems milestone, and study hour goals.
- **Export Placement Report (`.txt`)**: Generates an exhaustive placement review document ready to share with professors, mentors, or placement coordinators.
- **Automatic Persistence**: All data is automatically preserved in `student_tracker_data.dat`.

---

## 📁 Project Architecture

```
javaproject/
├── src/
│   ├── Main.java                          # Application entry point
│   ├── model/
│   │   ├── StudentProfile.java            # Academic profile & goals
│   │   ├── Task.java                      # Productivity tasks
│   │   ├── CodingLog.java                 # DSA session records
│   │   └── PlacementApplication.java      # Company drives & round pipeline
│   ├── service/
│   │   ├── StorageData.java               # Persistence data transfer container
│   │   ├── StorageService.java            # File I/O & Text report generator
│   │   └── TrackerService.java            # Scoring engine & state manager
│   └── ui/
│       ├── MainFrame.java                 # Modern Swing desktop GUI
│       └── ConsoleView.java               # Terminal CLI interface
├── run.bat                                # Windows 1-click launch (Desktop GUI)
├── run-cli.bat                            # Windows 1-click launch (CLI mode)
└── README.md
```

---

## 🚀 How to Run

### Option 1: Double-Click Batch File (Windows)
- Run `run.bat` for the **Desktop GUI**.
- Run `run-cli.bat` for the **Terminal CLI**.

### Option 2: Command Line (PowerShell / Command Prompt)

1. **Compile**:
```powershell
javac -encoding UTF-8 -d bin src\model\*.java src\service\*.java src\ui\*.java src\Main.java
```

2. **Run Desktop GUI**:
```powershell
java -cp bin Main
```

3. **Run Terminal Mode**:
```powershell
java -cp bin Main --cli
```

---

## 🧮 Placement Readiness Scoring Formula

$$\text{Readiness Score} = S_{\text{CGPA}} + S_{\text{DSA}} + S_{\text{Tasks}} + S_{\text{Pipeline}}$$

| Component | Max Points | Formula |
|---|---|---|
| **CGPA Standing** | 25 | $(\text{CGPA} / 10.0) \times 25$ |
| **DSA Milestone** | 35 | $\min(1.0, \frac{\text{Solved}}{\text{Target}}) \times 35$ |
| **Task Completion** | 20 | $(\text{Completion \%} / 100) \times 20$ |
| **Active Pipeline** | 20 | $\min(20, (\text{Active Drives} \times 3) + (\text{Offers} \times 10))$ |
| **Total** | **100** | Scaled from 0% to 100% |
