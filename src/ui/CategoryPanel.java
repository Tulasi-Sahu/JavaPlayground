package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class CategoryPanel extends JPanel {
    DefaultTableModel model;
    JTable table;

    public CategoryPanel() {
        setLayout(new BorderLayout());
        String[] cols = {"Category ID","Category Name","No of Books","Type"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JButton load = new JButton("Load Categories");
        load.addActionListener(e -> loadCats());
        add(load, BorderLayout.SOUTH);
    }

    private void loadCats() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT cat_id, cat_name, no_of_books, type FROM categories")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("cat_id"),
                    rs.getString("cat_name"),
                    rs.getInt("no_of_books"),
                    rs.getString("type")
                });
            }
            con.commit();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Load categories error: " + ex.getMessage());
        }
    }
}
