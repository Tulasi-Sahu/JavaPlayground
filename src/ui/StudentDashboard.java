package ui;
import java.awt.*;
import javax.swing.*;

public class StudentDashboard extends JFrame {
    private int studentId;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        setTitle("Student Dashboard");
        setSize(1000,640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Books", new BookPanel(studentId));
        tabs.add("Categories", new CategoryPanel());
        tabs.add("Reviews", new ReviewPanel(studentId));
        tabs.add("Profile", new StudentProfilePanel(studentId));

        // Bottom with Logout + Exit
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Logout");
        JButton exit = new JButton("Exit");
        logout.addActionListener(e -> {
            dispose();
            new StartFrame();
        });
        exit.addActionListener(e -> System.exit(0));
        bottom.add(logout);
        bottom.add(exit);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }
}
