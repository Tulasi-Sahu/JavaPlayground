package ui;
import javax.swing.*;

public class ExitPanel extends JPanel {
    public ExitPanel() {
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        add(exitBtn);
    }
}
