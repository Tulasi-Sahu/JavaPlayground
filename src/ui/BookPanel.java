package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class BookPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField nameField, authorField, categoryField, publisherField, bookIdField;
    private JLabel borrowCountLabel, activeBorrowLabel;
    private int studentId;

    public BookPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));

        // ====== 1. TABLE (Center) ======
        String[] cols = {"Book ID","Name","Author","Category","ISBN","Status","Pub Year","Publisher","Rating"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ====== 2. TOP SECTION (Search + Borrow/Return) ======
        JPanel topPanel = new JPanel(new GridLayout(1,2,10,10));

        // --- Search Filters ---
        JPanel searchPanel = new JPanel(new GridLayout(4,2,6,6));
        searchPanel.setBorder(BorderFactory.createTitledBorder(" Search Filters"));
        nameField = new JTextField();
        authorField = new JTextField();
        categoryField = new JTextField();
        publisherField = new JTextField();

        searchPanel.add(new JLabel("Book Name:")); searchPanel.add(nameField);
        searchPanel.add(new JLabel("Author:")); searchPanel.add(authorField);
        searchPanel.add(new JLabel("Category:")); searchPanel.add(categoryField);
        searchPanel.add(new JLabel("Publisher:")); searchPanel.add(publisherField);

        // --- Borrow / Return ---
        JPanel borrowPanel = new JPanel(new GridLayout(2,2,6,6));
        borrowPanel.setBorder(BorderFactory.createTitledBorder(" Borrow / Return"));
        bookIdField = new JTextField();

        borrowPanel.add(new JLabel("Book ID:")); 
        borrowPanel.add(bookIdField);
        borrowPanel.add(new JLabel("Note: Enter Book ID")); 
        borrowPanel.add(new JLabel("")); // spacer

        topPanel.add(searchPanel);
        topPanel.add(borrowPanel);

        add(topPanel, BorderLayout.NORTH);

        // ====== 3. BOTTOM SECTION (Stats + Buttons) ======
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // --- Borrow Stats ---
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        statsPanel.setBorder(BorderFactory.createTitledBorder(" Borrow Statistics"));
        borrowCountLabel = new JLabel("Total borrowed (lifetime): 0");
        activeBorrowLabel = new JLabel("Currently borrowed: 0");
        statsPanel.add(borrowCountLabel);
        statsPanel.add(activeBorrowLabel);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 10)); 
        JButton searchBtn = new JButton("Search");
        JButton loadBtn = new JButton("Load All");
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton sortYearBtn = new JButton("Sort by Year");
        JButton sortRatingBtn = new JButton("Sort by Rating");

        buttonPanel.add(searchBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(borrowBtn);
        buttonPanel.add(returnBtn);
        buttonPanel.add(sortYearBtn);
        buttonPanel.add(sortRatingBtn);

        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Button Actions =====
        searchBtn.addActionListener(e -> searchBooks());
        loadBtn.addActionListener(e -> loadBooks());
        borrowBtn.addActionListener(e -> borrowBook());
        returnBtn.addActionListener(e -> returnBook());
        sortYearBtn.addActionListener(e -> loadBooks("b.pub_year DESC"));
        sortRatingBtn.addActionListener(e -> loadBooks("b.rating DESC"));

        loadBooks();
        updateBorrowStats();
    }

    // === Load all books ===
    private void loadBooks() { loadBooks(null); }
    private void loadBooks(String orderBy) {
        model.setRowCount(0);
        String sql =
            "SELECT b.book_id, b.book_name, b.aut_name, c.cat_name, b.isbn, b.status, " +
            "b.pub_year, p.pub_name, b.rating " +
            "FROM books b " +
            "JOIN categories c ON b.cat_id = c.cat_id " +
            "JOIN publisher p ON b.pub_name = p.pub_name";
        if (orderBy != null) sql += " ORDER BY " + orderBy;

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("aut_name"),
                    rs.getString("cat_name"),
                    rs.getString("isbn"),
                    rs.getString("status"),
                    rs.getInt("pub_year"),
                    rs.getString("pub_name"),
                    rs.getInt("rating")
                });
            }
            con.commit();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // === Search books ===
    private void searchBooks() {
        model.setRowCount(0);
        String base =
            "SELECT b.book_id, b.book_name, b.aut_name, c.cat_name, b.isbn, b.status, " +
            "b.pub_year, p.pub_name, b.rating " +
            "FROM books b " +
            "JOIN categories c ON b.cat_id = c.cat_id " +
            "JOIN publisher p ON b.pub_name = p.pub_name WHERE 1=1";

        String n = nameField.getText().trim(),
               a = authorField.getText().trim(),
               c = categoryField.getText().trim(),
               p = publisherField.getText().trim();

        try (Connection con = DBConnection.getConnection()) {
            StringBuilder sb = new StringBuilder(base);
            if (!n.isEmpty()) sb.append(" AND LOWER(b.book_name) LIKE ?");
            if (!a.isEmpty()) sb.append(" AND LOWER(b.aut_name) LIKE ?");
            if (!c.isEmpty()) sb.append(" AND LOWER(c.cat_name) LIKE ?");
            if (!p.isEmpty()) sb.append(" AND LOWER(p.pub_name) LIKE ?");

            PreparedStatement ps = con.prepareStatement(sb.toString());
            int idx = 1;
            if (!n.isEmpty()) ps.setString(idx++, "%" + n.toLowerCase() + "%");
            if (!a.isEmpty()) ps.setString(idx++, "%" + a.toLowerCase() + "%");
            if (!c.isEmpty()) ps.setString(idx++, "%" + c.toLowerCase() + "%");
            if (!p.isEmpty()) ps.setString(idx++, "%" + p.toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("aut_name"),
                    rs.getString("cat_name"),
                    rs.getString("isbn"),
                    rs.getString("status"),
                    rs.getInt("pub_year"),
                    rs.getString("pub_name"),
                    rs.getInt("rating")
                });
            }
            con.commit();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // === Borrow book ===
    private void borrowBook() {
        String idText = bookIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Book ID to borrow");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int id = Integer.parseInt(idText);

            String upd = "UPDATE books SET status='Borrowed' WHERE book_id=? AND UPPER(status)='AVAILABLE'";
            PreparedStatement ps = con.prepareStatement(upd);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE student SET downloaded_books = NVL(downloaded_books,0) + 1 WHERE stu_id=?"
                );
                ps2.setInt(1, studentId);
                ps2.executeUpdate();

                con.commit();
                JOptionPane.showMessageDialog(this, "Book borrowed successfully.");
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Book not available to borrow.");
            }
            loadBooks();
            updateBorrowStats();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Book ID must be a number!");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // === Return book ===
    private void returnBook() {
        String idText = bookIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Book ID to return");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            int id = Integer.parseInt(idText);

            String upd = "UPDATE books SET status='Available' WHERE book_id=? AND UPPER(status)='BORROWED'";
            PreparedStatement ps = con.prepareStatement(upd);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                con.commit();
                JOptionPane.showMessageDialog(this, "Book returned successfully.");
            } else {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Book was not borrowed.");
            }
            loadBooks();
            updateBorrowStats();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Book ID must be a number!");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    // === Borrow stats ===
    private void updateBorrowStats() {
        try (Connection con = DBConnection.getConnection()) {
            // Lifetime downloads
            PreparedStatement ps1 = con.prepareStatement("SELECT downloaded_books FROM student WHERE stu_id=?");
            ps1.setInt(1, studentId);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                borrowCountLabel.setText("Total borrowed (lifetime): " + rs1.getInt(1));
            }

            // Currently borrowed
            PreparedStatement ps2 = con.prepareStatement("SELECT COUNT(*) FROM books WHERE status='Borrowed'");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                activeBorrowLabel.setText("Currently borrowed: " + rs2.getInt(1));
            }
            con.commit();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
