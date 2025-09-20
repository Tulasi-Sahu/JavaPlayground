package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class CategoryAdminPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField idField, nameField, noOfBooksField, typeField;

    public CategoryAdminPanel() {
        setLayout(new BorderLayout(6,6));
        String[] cols = {"Cat ID","Cat Name","No of Books","Type"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2,4,6,6));
        idField = new JTextField(); nameField = new JTextField(); noOfBooksField = new JTextField(); typeField = new JTextField();
        form.add(new JLabel("Cat ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("No of Books:")); form.add(noOfBooksField);
        form.add(new JLabel("Type:")); form.add(typeField);
        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,8,6));
        JButton load = new JButton("Load"); JButton add = new JButton("Add"); JButton update = new JButton("Update"); JButton delete = new JButton("Delete");
        buttons.add(load); buttons.add(add); buttons.add(update); buttons.add(delete);
        add(buttons, BorderLayout.SOUTH);

        load.addActionListener(e -> loadCats());
        add.addActionListener(e -> addCat());
        update.addActionListener(e -> updateCat());
        delete.addActionListener(e -> deleteCat());

        loadCats();
    }

    private void loadCats() {
        model.setRowCount(0);
        String sql = "SELECT cat_id, cat_name, no_of_books, type FROM categories";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)});
            }
            con.commit();
        } catch (Exception ex) { showError(ex); }
    }

    private void addCat() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID and Name required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO categories(cat_id,cat_name,no_of_books,type) VALUES(?,?,?,?)");
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            ps.setString(2, nameField.getText().trim());
            ps.setInt(3, noOfBooksField.getText().trim().isEmpty()?0:Integer.parseInt(noOfBooksField.getText().trim()));
            ps.setString(4, typeField.getText().trim());
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Category added" : "Add failed");
            loadCats();
        } catch (Exception ex) { showError(ex); }
    }

    private void updateCat() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE categories SET cat_name=?, no_of_books=?, type=? WHERE cat_id=?");
            ps.setString(1, nameField.getText().trim());
            ps.setInt(2, noOfBooksField.getText().trim().isEmpty()?0:Integer.parseInt(noOfBooksField.getText().trim()));
            ps.setString(3, typeField.getText().trim());
            ps.setInt(4, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Category updated" : "Not found");
            loadCats();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteCat() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM categories WHERE cat_id=?");
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Category deleted" : "Not found");
            loadCats();
        } catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
