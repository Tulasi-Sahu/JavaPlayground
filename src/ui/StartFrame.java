package ui;
import javax.swing.*;

public class StartFrame extends JFrame {
    public StartFrame() {
        setTitle("Personal Library - Start");
        setSize(360,160);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton studentBtn = new JButton("Student Login");
        JButton adminBtn = new JButton("Admin Login");

        studentBtn.addActionListener(e -> {
            new StudentLoginFrame();
            dispose();
        });

        // admin flow left as before (you already have AdminLoginFrame)
        adminBtn.addActionListener(e -> {
            new AdminLoginFrame();
            dispose();
        });

        JPanel p = new JPanel();
        p.add(studentBtn);
        p.add(adminBtn);
        add(p);
        setVisible(true);
    }
}
