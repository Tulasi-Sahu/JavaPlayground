package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class ReviewPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<Integer> bookIdCombo;
    private JTextField reviewText;
    private int studentId;

    public ReviewPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10,10));

        // ===== Table Section =====
        String[] cols = {"Book ID","Review","Date"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Add Review Section =====
        JPanel addPanel = new JPanel(new GridLayout(2,2,5,5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add Review"));

        bookIdCombo = new JComboBox<>();
        reviewText = new JTextField();

        addPanel.add(new JLabel("Book ID:")); addPanel.add(bookIdCombo);
        addPanel.add(new JLabel("Review:")); addPanel.add(reviewText);

        add(addPanel, BorderLayout.NORTH);

        // ===== Buttons Section =====
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addBtn = new JButton("Add Review");
        JButton delBtn = new JButton("Delete Review");
        JButton loadBtn = new JButton("Load My Reviews");

        buttons.add(addBtn); buttons.add(delBtn); buttons.add(loadBtn);

        add(buttons, BorderLayout.SOUTH);

        // === Button Actions ===
        addBtn.addActionListener(e -> addReview());
        delBtn.addActionListener(e -> deleteReview());
        loadBtn.addActionListener(e -> loadReviews());

        loadBooksForCombo();
        loadReviews();
    }

    private void loadBooksForCombo() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT book_id FROM books ORDER BY book_id")) {
            while (rs.next()) bookIdCombo.addItem(rs.getInt(1));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadReviews() {
        model.setRowCount(0);
        String sql = "SELECT book_id, rev_text, rev_date FROM review WHERE stu_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("rev_text"),
                    rs.getDate("rev_date")
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addReview() {
        String text = reviewText.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a review text");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO review (book_id, stu_id, rev_text, rev_date) VALUES (?, ?, ?, SYSDATE)")) {
            ps.setInt(1, (Integer) bookIdCombo.getSelectedItem());
            ps.setInt(2, studentId);
            ps.setString(3, text);
            ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, "Review added successfully!");
            reviewText.setText("");
            loadReviews();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteReview() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a review to delete");
            return;
        }
        int bookId = (int) model.getValueAt(row, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM review WHERE book_id=? AND stu_id=?")) {
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, "Review deleted successfully!");
            loadReviews();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
