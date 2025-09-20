package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class BookAdminPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField idField, nameField, authorField, catIdField, catNameField, isbnField, statusField, pubYearField, pubNameField, ratingField;

    public BookAdminPanel() {
        setLayout(new BorderLayout(8,8));
        String[] cols = {"Book ID","Name","Author","Cat ID","Cat Name","ISBN","Status","Pub Year","Pub Name","Rating"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form on top
        JPanel form = new JPanel(new GridLayout(5,4,6,6));
        idField = new JTextField(); nameField = new JTextField(); authorField = new JTextField();
        catIdField = new JTextField(); catNameField = new JTextField(); isbnField = new JTextField();
        statusField = new JTextField(); pubYearField = new JTextField(); pubNameField = new JTextField();
        ratingField = new JTextField();

        form.add(new JLabel("Book ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Author:")); form.add(authorField);
        form.add(new JLabel("Cat ID:")); form.add(catIdField);
        form.add(new JLabel("Cat Name:")); form.add(catNameField);
        form.add(new JLabel("ISBN:")); form.add(isbnField);
        form.add(new JLabel("Status:")); form.add(statusField);
        form.add(new JLabel("Pub Year:")); form.add(pubYearField);
        form.add(new JLabel("Pub Name:")); form.add(pubNameField);
        form.add(new JLabel("Rating:")); form.add(ratingField);

        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,10,6));
        JButton load = new JButton("Load"); JButton add = new JButton("Add");
        JButton update = new JButton("Update"); JButton delete = new JButton("Delete");
        buttons.add(load); buttons.add(add); buttons.add(update); buttons.add(delete);
        add(buttons, BorderLayout.SOUTH);

        load.addActionListener(e -> loadBooks());
        add.addActionListener(e -> addBook());
        update.addActionListener(e -> updateBook());
        delete.addActionListener(e -> deleteBook());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                idField.setText(String.valueOf(model.getValueAt(r,0)));
                nameField.setText(String.valueOf(model.getValueAt(r,1)));
                authorField.setText(String.valueOf(model.getValueAt(r,2)));
                catIdField.setText(String.valueOf(model.getValueAt(r,3)));
                catNameField.setText(String.valueOf(model.getValueAt(r,4)));
                isbnField.setText(String.valueOf(model.getValueAt(r,5)));
                statusField.setText(String.valueOf(model.getValueAt(r,6)));
                pubYearField.setText(String.valueOf(model.getValueAt(r,7)));
                pubNameField.setText(String.valueOf(model.getValueAt(r,8)));
                ratingField.setText(String.valueOf(model.getValueAt(r,9)));
            }
        });

        loadBooks();
    }

    private void loadBooks() {
        model.setRowCount(0);
        String sql = "SELECT book_id, book_name, aut_name, cat_id, cat_name, isbn, status, pub_year, pub_name, rating FROM books";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("book_name"),
                    rs.getString("aut_name"),
                    rs.getInt("cat_id"),
                    rs.getString("cat_name"),
                    rs.getString("isbn"),
                    rs.getString("status"),
                    rs.getInt("pub_year"),
                    rs.getString("pub_name"),
                    rs.getInt("rating")
                });
            }
            con.commit();
        } catch (Exception ex) { showError(ex); }
    }

    private void addBook() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book ID and Name required");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO books(book_id,book_name,aut_name,cat_id,cat_name,isbn,status,pub_year,pub_name,rating) VALUES(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, authorField.getText().trim());
            ps.setInt(4, Integer.parseInt(catIdField.getText().trim()));
            ps.setString(5, catNameField.getText().trim());
            ps.setString(6, isbnField.getText().trim());
            ps.setString(7, statusField.getText().trim().isEmpty() ? "Available" : statusField.getText().trim());
            ps.setInt(8, pubYearField.getText().trim().isEmpty() ? 0 : Integer.parseInt(pubYearField.getText().trim()));
            ps.setString(9, pubNameField.getText().trim());
            ps.setInt(10, ratingField.getText().trim().isEmpty()?0:Integer.parseInt(ratingField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Book added" : "Add failed");
            loadBooks();
        } catch (Exception ex) { showError(ex); }
    }

    private void updateBook() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Book ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            String sql = "UPDATE books SET book_name=?, aut_name=?, cat_id=?, cat_name=?, isbn=?, status=?, pub_year=?, pub_name=?, rating=? WHERE book_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, authorField.getText().trim());
            ps.setInt(3, Integer.parseInt(catIdField.getText().trim()));
            ps.setString(4, catNameField.getText().trim());
            ps.setString(5, isbnField.getText().trim());
            ps.setString(6, statusField.getText().trim());
            ps.setInt(7, pubYearField.getText().trim().isEmpty()?0:Integer.parseInt(pubYearField.getText().trim()));
            ps.setString(8, pubNameField.getText().trim());
            ps.setInt(9, ratingField.getText().trim().isEmpty()?0:Integer.parseInt(ratingField.getText().trim()));
            ps.setInt(10, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Book updated" : "Not found");
            loadBooks();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteBook() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Book ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM books WHERE book_id=?");
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Book deleted" : "Not found");
            loadBooks();
        } catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
