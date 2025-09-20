package ui;

import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    private int adminId;

    public AdminDashboard(int adminId) {
        this.adminId = adminId;
        setTitle("Admin Dashboard");
        setSize(1100,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Books", new BookAdminPanel());
        tabs.add("Categories", new CategoryAdminPanel());
        tabs.add("Students", new StudentAdminPanel());
        tabs.add("Reviews", new ReviewAdminPanel());

        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        JButton logout = new JButton("Logout");
        JButton exit = new JButton("Exit");
        bottom.add(logout); bottom.add(exit);
        add(bottom, BorderLayout.SOUTH);

        logout.addActionListener(e -> {
            dispose();
            new StartFrame().setVisible(true);
        });
        exit.addActionListener(e -> System.exit(0));
    }
}
