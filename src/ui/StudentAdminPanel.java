package ui;
import db.DBConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class StudentAdminPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField idField, nameField, usernameField, passwordField, contactField, emailField, branchField, yearField, downloadedField;

    public StudentAdminPanel() {
        setLayout(new BorderLayout(6,6));
        String[] cols = {"Stu ID","Name","Username","Password","Contact","Email","Branch","Year","Downloaded"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(5,4,6,6));
        idField = new JTextField(); nameField = new JTextField(); usernameField = new JTextField();
        passwordField = new JTextField(); contactField = new JTextField(); emailField = new JTextField();
        branchField = new JTextField(); yearField = new JTextField(); downloadedField = new JTextField();

        form.add(new JLabel("Stu ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Username:")); form.add(usernameField);
        form.add(new JLabel("Password:")); form.add(passwordField);
        form.add(new JLabel("Contact:")); form.add(contactField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Branch:")); form.add(branchField);
        form.add(new JLabel("Year:")); form.add(yearField);
        form.add(new JLabel("Downloaded:")); form.add(downloadedField);

        add(form, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,8,6));
        JButton load = new JButton("Load"); JButton add = new JButton("Add"); JButton update = new JButton("Update"); JButton delete = new JButton("Delete"); JButton resetPwd = new JButton("Reset Password");
        buttons.add(load); buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(resetPwd);
        add(buttons, BorderLayout.SOUTH);

        load.addActionListener(e -> loadStudents());
        add.addActionListener(e -> addStudent());
        update.addActionListener(e -> updateStudent());
        delete.addActionListener(e -> deleteStudent());
        resetPwd.addActionListener(e -> resetPassword());

        loadStudents();
    }

    private void loadStudents() {
        model.setRowCount(0);
        String sql = "SELECT stu_id, name, username, password, contact, email, branch, year, downloaded_books FROM student";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("stu_id"), rs.getString("name"), rs.getString("username"),
                    rs.getString("password"), rs.getString("contact"), rs.getString("email"),
                    rs.getString("branch"), rs.getInt("year"), rs.getInt("downloaded_books")
                });
            }
            con.commit();
        } catch (Exception ex) { showError(ex); }
    }

    private void addStudent() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID and Name required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO student(stu_id,name,username,password,contact,email,branch,year,downloaded_books) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            ps.setString(2, nameField.getText().trim());
            ps.setString(3, usernameField.getText().trim());
            ps.setString(4, passwordField.getText().trim());
            ps.setString(5, contactField.getText().trim());
            ps.setString(6, emailField.getText().trim());
            ps.setString(7, branchField.getText().trim());
            ps.setInt(8, yearField.getText().trim().isEmpty()?0:Integer.parseInt(yearField.getText().trim()));
            ps.setInt(9, downloadedField.getText().trim().isEmpty()?0:Integer.parseInt(downloadedField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Student added" : "Add failed");
            loadStudents();
        } catch (Exception ex) { showError(ex); }
    }

    private void updateStudent() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE student SET name=?, username=?, password=?, contact=?, email=?, branch=?, year=?, downloaded_books=? WHERE stu_id=?");
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, usernameField.getText().trim());
            ps.setString(3, passwordField.getText().trim());
            ps.setString(4, contactField.getText().trim());
            ps.setString(5, emailField.getText().trim());
            ps.setString(6, branchField.getText().trim());
            ps.setInt(7, yearField.getText().trim().isEmpty()?0:Integer.parseInt(yearField.getText().trim()));
            ps.setInt(8, downloadedField.getText().trim().isEmpty()?0:Integer.parseInt(downloadedField.getText().trim()));
            ps.setInt(9, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Student updated" : "Not found");
            loadStudents();
        } catch (Exception ex) { showError(ex); }
    }

    private void deleteStudent() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM student WHERE stu_id=?");
            ps.setInt(1, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Student deleted" : "Not found");
            loadStudents();
        } catch (Exception ex) { showError(ex); }
    }

    private void resetPassword() {
        if (idField.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required"); return; }
        String newPwd = JOptionPane.showInputDialog(this, "Enter new password:");
        if (newPwd == null || newPwd.trim().isEmpty()) return;
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE student SET password=? WHERE stu_id=?");
            ps.setString(1, newPwd.trim());
            ps.setInt(2, Integer.parseInt(idField.getText().trim()));
            int r = ps.executeUpdate();
            con.commit();
            JOptionPane.showMessageDialog(this, r>0 ? "Password reset" : "Not found");
            loadStudents();
        } catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
