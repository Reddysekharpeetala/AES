import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class pro_view extends JPanel implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn, deleteBtn, updateBtn, searchBtn;
    JScrollPane scrollPane;

    public pro_view() {
        setLayout(new BorderLayout(10, 10)); // Add spacing

        // Table columns
        String[] columns = {"Program ID", "Program Name", "Program Type", "Duration", "Department"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Table customization for bigger display
        table.setRowHeight(40); // Taller rows
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Allow horizontal scroll
        table.setFillsViewportHeight(true);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Program ID
        table.getColumnModel().getColumn(1).setPreferredWidth(400); // Program Name
        table.getColumnModel().getColumn(2).setPreferredWidth(300); // Program Type
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Duration
        table.getColumnModel().getColumn(4).setPreferredWidth(250); // Department

        // Scroll pane
        scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1600, 800));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        searchBtn = new JButton("Search");
        refreshBtn = new JButton("Refresh");
        deleteBtn = new JButton("Delete");
        updateBtn = new JButton("Update");

        buttonPanel.add(searchBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(updateBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add button listeners
        searchBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        updateBtn.addActionListener(this);

        // Load data initially
        loadData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == refreshBtn) {
            loadData();
        } else if (src == deleteBtn) {
            deleteSelectedRow();
        } else if (src == updateBtn) {
            updateSelectedRow();
        } else if (src == searchBtn) {
            searchProgram();
        }
    }

    // Load all programs
    public void loadData() {
        try {
            model.setRowCount(0); // Clear table
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan","root","reddy123");
            String sql = "SELECT * FROM programs";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while(rs.next()) {
                Object[] row = {
                        rs.getString("program_id"),
                        rs.getString("program_name"),
                        rs.getString("program_type"),
                        rs.getString("duration"),
                        rs.getString("department")
                };
                model.addRow(row);
            }
            con.close();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    // Delete selected row
    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        String id = (String) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this program?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan","root","reddy123")) {
            String sql = "DELETE FROM programs WHERE program_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Program deleted successfully.");
            loadData();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Update selected row
    private void updateSelectedRow() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        String progID = (String) model.getValueAt(row, 0);
        JTextField nameField = new JTextField((String) model.getValueAt(row, 1));
        JTextField typeField = new JTextField((String) model.getValueAt(row, 2));
        JTextField duraField = new JTextField((String) model.getValueAt(row, 3));
        JTextField deptField = new JTextField((String) model.getValueAt(row, 4));

        JPanel panel = new JPanel(new GridLayout(4,2,5,5));
        panel.add(new JLabel("Program Name:")); panel.add(nameField);
        panel.add(new JLabel("Program Type:")); panel.add(typeField);
        panel.add(new JLabel("Duration:")); panel.add(duraField);
        panel.add(new JLabel("Department:")); panel.add(deptField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Update Program", JOptionPane.OK_CANCEL_OPTION);
        if(option != JOptionPane.OK_OPTION) return;

        try(Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan","root","reddy123")) {
            String sql = "UPDATE programs SET program_name=?, program_type=?, duration=?, department=? WHERE program_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, nameField.getText());
            pst.setString(2, typeField.getText());
            pst.setString(3, duraField.getText());
            pst.setString(4, deptField.getText());
            pst.setString(5, progID);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Program updated successfully!");
            loadData();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Search program by ID or Name
    private void searchProgram() {
        String input = JOptionPane.showInputDialog(this, "Enter Program ID or Name:");
        if(input == null || input.trim().isEmpty()) return;

        try {
            model.setRowCount(0);
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan","root","reddy123");

            String sql = "SELECT * FROM programs WHERE program_id=? OR program_name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, input);
            pst.setString(2, "%" + input + "%");

            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                Object[] row = {
                        rs.getString("program_id"),
                        rs.getString("program_name"),
                        rs.getString("program_type"),
                        rs.getString("duration"),
                        rs.getString("department")
                };
                model.addRow(row);
            }
            con.close();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
