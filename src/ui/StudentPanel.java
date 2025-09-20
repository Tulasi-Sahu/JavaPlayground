package ui;
import db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class StudentPanel extends JPanel {
    JTable table;
    DefaultTableModel model;

    public StudentPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        String[] cols = {"ID","Name","Username","Branch","Year","Downloads"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table));

        JButton loadBtn = new JButton("Load Students");
        loadBtn.addActionListener(e -> loadStudents());
        add(loadBtn);
    }

    private void loadStudents() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM student");
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("stu_id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("branch"),
                    rs.getInt("year"),
                    rs.getInt("downloaded_books")
                });
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
