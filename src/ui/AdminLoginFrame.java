package ui;

import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AdminLoginFrame extends JFrame {
    private JTextField idField = new JTextField(10);
    private JPasswordField passField = new JPasswordField(10);

    public AdminLoginFrame() {
        setTitle("Admin Login");
        setSize(360,180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(3,2,8,8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        
        formPanel.add(new JLabel("Admin ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);

        JButton back = new JButton("Back");
        JButton login = new JButton("Login");
        formPanel.add(back);
        formPanel.add(login);

        add(formPanel);

        back.addActionListener(e -> {
            new StartFrame();
            dispose();
        });

        login.addActionListener(e -> doLogin());
        setVisible(true);
    }

    private void doLogin() {
        String idText = idField.getText().trim();
        String pwd = new String(passField.getPassword()).trim();

        if (idText.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter admin id and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT DISTINCT ad_id FROM administrator WHERE ad_id=? AND ad_password=?";
            PreparedStatement ps = con.prepareStatement(sql);

            // Admin ID is INT as per your schema
            ps.setInt(1, Integer.parseInt(idText));
            ps.setString(2, pwd);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int aid = rs.getInt("ad_id");
                JOptionPane.showMessageDialog(this, "Login successful!");
                new AdminDashboard(aid).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials");
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Admin ID must be a number");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
        }
    }
}
