import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class view_subject extends JPanel implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn, deleteBtn, searchBtn, updateBtn, filterBtn;
    JComboBox<String> semesterCombo, programCombo;
    JScrollPane scrollPane;

    public view_subject() {
        setLayout(new BorderLayout(10, 10));

        // Table columns
        String[] columns = {"S.No", "Subject Name", "Category", "Program Name", "Semester", "ID"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        // Hide ID column
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Top filter panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Select Semester:"));
        semesterCombo = new JComboBox<>();
        semesterCombo.addItem("All");
        for (int i = 1; i <= 6; i++) semesterCombo.addItem("Semester " + i);
        topPanel.add(semesterCombo);

        topPanel.add(new JLabel("Select Program:"));
        programCombo = new JComboBox<>();
        programCombo.addItem("All");
        loadPrograms();
        topPanel.add(programCombo);

        filterBtn = new JButton("Filter");
        topPanel.add(filterBtn);
        add(topPanel, BorderLayout.NORTH);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchBtn = new JButton("Search");
        refreshBtn = new JButton("Refresh");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");

        buttonPanel.add(searchBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Listeners
        searchBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        filterBtn.addActionListener(this);

        // Load all subjects and minors initially
        loadData();
    }

    // --- Load programs ---
    private void loadPrograms() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {
            String sql = "SELECT program_name FROM programs";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                programCombo.addItem(rs.getString("program_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading programs: " + e.getMessage());
        }
    }

    // --- Load subjects and minor subjects ---
    private void loadData() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            int serial = 1;

            // Load main subjects
            String sql = "SELECT subject_id, subject_name, category, program_name, semester FROM subjects";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name") != null ? rs.getString("program_name") : "N/A",
                        rs.getString("semester"),
                        rs.getInt("subject_id")
                };
                model.addRow(row);
            }

            // Load minor subjects
            sql = "SELECT minor_id, subject_name, category, program_name, semester FROM minor_subjects";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name") != null ? rs.getString("program_name") : "N/A",
                        rs.getString("semester"),
                        rs.getInt("minor_id")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage());
        }
    }

    // --- Filter subjects and minors ---
    public void loadByFilter(String semester, String programName) {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            int serial = 1;

            // Load filtered main subjects
            String sql = "SELECT subject_id, subject_name, category, program_name, semester FROM subjects WHERE 1=1";
            if (!semester.equals("All")) sql += " AND semester=?";
            if (!programName.equals("All")) sql += " AND program_name=?";

            PreparedStatement pst = con.prepareStatement(sql);
            int index = 1;
            if (!semester.equals("All")) pst.setString(index++, semester);
            if (!programName.equals("All")) pst.setString(index, programName);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name") != null ? rs.getString("program_name") : "N/A",
                        rs.getString("semester"),
                        rs.getInt("subject_id")
                };
                model.addRow(row);
            }

            // Load filtered minor subjects
            sql = "SELECT minor_id, subject_name, category, program_name, semester FROM minor_subjects WHERE 1=1";
            if (!semester.equals("All")) sql += " AND semester=?";
            if (!programName.equals("All")) sql += " AND program_name=?";

            pst = con.prepareStatement(sql);
            index = 1;
            if (!semester.equals("All")) pst.setString(index++, semester);
            if (!programName.equals("All")) pst.setString(index, programName);

            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name") != null ? rs.getString("program_name") : "N/A",
                        rs.getString("semester"),
                        rs.getInt("minor_id")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering subjects: " + e.getMessage());
        }
    }

    // --- Delete selected row ---
    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 5);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this subject?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            String tableName = "subjects";
            // Check if it's minor subject by category in hidden ID (optional, adjust logic if needed)
            String sqlCheck = "SELECT 'minor' AS type FROM minor_subjects WHERE minor_id=?";
            PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
            pstCheck.setInt(1, id);
            ResultSet rsCheck = pstCheck.executeQuery();
            if (rsCheck.next()) tableName = "minor_subjects";

            String sql = "DELETE FROM " + tableName + " WHERE " + (tableName.equals("subjects") ? "subject_id" : "minor_id") + "=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject deleted successfully!");
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Search ---
    private void searchSubject() {
        String input = JOptionPane.showInputDialog(this, "Enter Subject Name:");
        if (input == null || input.trim().isEmpty()) return;

        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            int serial = 1;

            // Search main subjects
            String sql = "SELECT subject_id, subject_name, category, program_name, semester FROM subjects WHERE subject_name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + input + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name"),
                        rs.getString("semester"),
                        rs.getInt("subject_id")
                };
                model.addRow(row);
            }

            // Search minor subjects
            sql = "SELECT minor_id, subject_name, category, program_name, semester FROM minor_subjects WHERE subject_name LIKE ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + input + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name"),
                        rs.getString("semester"),
                        rs.getInt("minor_id")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Update ---
    private void updateSubject() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a subject to update.");
            return;
        }

        int id = (int) model.getValueAt(row, 5);
        String currentName = (String) model.getValueAt(row, 1);
        String currentCategory = (String) model.getValueAt(row, 2);
        String currentProgram = (String) model.getValueAt(row, 3);
        String currentSemester = (String) model.getValueAt(row, 4);

        JTextField nameField = new JTextField(currentName);
        String[] categories = {"Languages", "Multidisciplinary", "Major", "Minor", "Skills"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setSelectedItem(currentCategory);

        JComboBox<String> semesterBox = new JComboBox<>();
        for (int i = 1; i <= 6; i++) semesterBox.addItem("Semester " + i);
        semesterBox.setSelectedItem(currentSemester);

        JComboBox<String> programBox = new JComboBox<>();
        for (int i = 1; i < programCombo.getItemCount(); i++) programBox.addItem(programCombo.getItemAt(i));
        programBox.setSelectedItem(currentProgram);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Category:")); panel.add(categoryBox);
        panel.add(new JLabel("Program:")); panel.add(programBox);
        panel.add(new JLabel("Semester:")); panel.add(semesterBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Subject",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

                String tableName = "subjects";
                String sqlCheck = "SELECT 'minor' AS type FROM minor_subjects WHERE minor_id=?";
                PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
                pstCheck.setInt(1, id);
                ResultSet rsCheck = pstCheck.executeQuery();
                if (rsCheck.next()) tableName = "minor_subjects";

                String sql = "UPDATE " + tableName + " SET subject_name=?, category=?, program_name=?, semester=? " +
                        "WHERE " + (tableName.equals("subjects") ? "subject_id" : "minor_id") + "=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, (String) categoryBox.getSelectedItem());
                pst.setString(3, (String) programBox.getSelectedItem());
                pst.setString(4, (String) semesterBox.getSelectedItem());
                pst.setInt(5, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Subject updated successfully!");
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == refreshBtn) loadData();
        else if (src == deleteBtn) deleteSelectedRow();
        else if (src == searchBtn) searchSubject();
        else if (src == updateBtn) updateSubject();
        else if (src == filterBtn) {
            String semester = (String) semesterCombo.getSelectedItem();
            String program = (String) programCombo.getSelectedItem();
            loadByFilter(semester, program);
        }
    }
}
