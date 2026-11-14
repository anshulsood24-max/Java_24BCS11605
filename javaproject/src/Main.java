import service.TrackerService;
import ui.ConsoleView;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for Student Productivity & Placement Tracker.
 * 
 * Supports both:
 *  1. Desktop Graphical Interface (Swing GUI) - Default
 *  2. Terminal / Command-line Interface (CLI) - via argument '--cli' or in headless environments
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("    STUDENT PRODUCTIVITY & PLACEMENT TRACKER (Java)       ");
        System.out.println("==========================================================");

        TrackerService trackerService = new TrackerService();

        boolean forceCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceCli = true;
                break;
            }
        }

        if (forceCli || GraphicsEnvironment.isHeadless()) {
            System.out.println("Starting in Command-Line Interface (CLI) mode...\n");
            ConsoleView consoleView = new ConsoleView(trackerService);
            consoleView.start();
        } else {
            System.out.println("Launching Desktop GUI...\n(Pass '--cli' or '-c' to run in terminal mode instead.)\n");
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame(trackerService);
                    frame.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Failed to launch GUI: " + e.getMessage());
                    System.out.println("Falling back to CLI mode...");
                    ConsoleView consoleView = new ConsoleView(trackerService);
                    consoleView.start();
                }
            });
        }
    }
}
