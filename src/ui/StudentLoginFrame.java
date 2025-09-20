package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class StudentLoginFrame extends JFrame {
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);

    public StudentLoginFrame() {
        setTitle("Student Login");
        setSize(360,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3,2,6,6));
        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        JButton login = new JButton("Login");
        JButton back = new JButton("Back");
        panel.add(back);
        panel.add(login);
        add(panel);

        back.addActionListener(e -> {
            new StartFrame();
            dispose();
        });

        login.addActionListener(e -> doLogin());
        setVisible(true);
    }

    private void doLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT stu_id, name FROM student WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int sid = rs.getInt("stu_id");
                String name = rs.getString("name");
                JOptionPane.showMessageDialog(this, "Welcome, " + name);
                new StudentDashboard(sid);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Student Login!");
            }
            con.commit();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
        }
    }
}
