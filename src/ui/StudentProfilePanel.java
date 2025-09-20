package ui;
import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentProfilePanel extends JPanel {
    private int studentId;
    private JLabel infoLabel;

    public StudentProfilePanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout());
        infoLabel = new JLabel();
        add(new JScrollPane(infoLabel), BorderLayout.CENTER);
        JButton load = new JButton("Load Profile");
        load.addActionListener(e -> loadProfile());
        add(load, BorderLayout.SOUTH);
    }

    private void loadProfile() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT stu_id, name, username, contact, email, branch, year, downloaded_books FROM student WHERE stu_id=?")) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String html = "<html><body style='font-family:Arial'>";
                html += "<h3>Student Profile</h3>";
                html += "<b>ID:</b> " + rs.getInt("stu_id") + "<br/>";
                html += "<b>Name:</b> " + rs.getString("name") + "<br/>";
                html += "<b>Username:</b> " + rs.getString("username") + "<br/>";
                html += "<b>Contact:</b> " + rs.getString("contact") + "<br/>";
                html += "<b>Email:</b> " + rs.getString("email") + "<br/>";
                html += "<b>Branch:</b> " + rs.getString("branch") + "<br/>";
                html += "<b>Year:</b> " + rs.getInt("year") + "<br/>";
                html += "<b>Downloaded Books:</b> " + rs.getInt("downloaded_books") + "<br/>";
                html += "</body></html>";
                infoLabel.setText(html);
            } else {
                infoLabel.setText("Profile not found");
            }
            con.commit();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Profile load error: " + ex.getMessage());
        }
    }
}
