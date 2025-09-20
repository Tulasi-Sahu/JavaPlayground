package ui;
import javax.swing.*;

public class LogoutPanel extends JPanel {
    public LogoutPanel(JFrame parent) {
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            parent.dispose();
            new StartFrame();
        });
        add(logoutBtn);
    }
}
