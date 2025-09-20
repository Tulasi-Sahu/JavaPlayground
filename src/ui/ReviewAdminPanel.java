package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class ReviewAdminPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField bookIdField, stuIdField;

    public ReviewAdminPanel() {
        setLayout(new BorderLayout(6,6));
        String[] cols = {"Book ID","Student ID","Review","Date"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel filter = new JPanel(new GridLayout(1,4,6,6));
        bookIdField = new JTextField(); stuIdField = new JTextField();
        filter.add(new JLabel("Book ID:")); filter.add(bookIdField);
        filter.add(new JLabel("Student ID:")); filter.add(stuIdField);
        add(filter, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,8,6));
        JButton load = new JButton("Load"); JButton delete = new JButton("Delete Selected"); JButton filterBtn = new JButton("Filter");
        buttons.add(load); buttons.add(filterBtn); buttons.add(delete);
        add(buttons, BorderLayout.SOUTH);

        load.addActionListener(e -> loadReviews(null,null));
        filterBtn.addActionListener(e -> loadReviews(bookIdField.getText().trim(), stuIdField.getText().trim()));
        delete.addActionListener(e -> deleteSelected());

        loadReviews(null,null);
    }

    private void loadReviews(String bookId, String stuId) {
        model.setRowCount(0);
        StringBuilder sb = new StringBuilder("SELECT book_id, stu_id, rev_text, rev_date FROM review WHERE 1=1");
        if (bookId != null && !bookId.isEmpty()) sb.append(" AND book_id=").append(bookId);
        if (stuId != null && !stuId.isEmpty()) sb.append(" AND stu_id=").append(stuId);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sb.toString())) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDate(4)});
            }
            con.commit();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a review to delete"); return; }
        int bookId = (int) model.getValueAt(r,0);
        int stuId = (int) model.getValueAt(r,1);
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM review WHERE book_id=? AND stu_id=? AND rev_text=?");
            ps.setInt(1, bookId);
            ps.setInt(2, stuId);
            ps.setString(3, (String) model.getValueAt(r,2));
            int res = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, res>0 ? "Review deleted" : "Delete failed");
            loadReviews(null,null);
        } catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
