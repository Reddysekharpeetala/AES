import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class fac_view extends JPanel implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JButton refreshBtn, deleteBtn, updateBtn, searchBtn;
    java.util.List<Integer> idList = new java.util.ArrayList<>(); // Store actual IDs

    fac_view() {
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"ID","Name","DOB","Email","Phone","Department","Qualification","Experience","Address"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {50,150,120,200,120,150,150,120,250};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchBtn = new JButton("Search");
        refreshBtn = new JButton("Refresh");
        deleteBtn = new JButton("Delete");
        updateBtn = new JButton("Update");

        buttonPanel.add(searchBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(updateBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        updateBtn.addActionListener(this);

        loadData();
    }

    public void loadData() {
        try {
            model.setRowCount(0);
            idList.clear();

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan","root","reddy123");
            String sql = "SELECT * FROM faculty_details";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            int serial = 1;
            while(rs.next()) {
                int id = rs.getInt("id");
                idList.add(id); // Store actual id
                Object[] row = {
                    serial++, // Show continuous serial number
                    rs.getString("emp_name"),
                    rs.getDate("dob"),
                    rs.getString("emp_email"),
                    rs.getString("emp_ph"),
                    rs.getString("emp_dept"),
                    rs.getString("emp_qua"),
                    rs.getString("emp_exp"),
                    rs.getString("address")
                };
                model.addRow(row);
            }
            con.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == refreshBtn) loadData();
        else if(src == deleteBtn) deleteSelectedRow();
        else if(src == updateBtn) updateSelectedRow();
        else if(src == searchBtn) searchFaculty();
    }

    private void deleteSelectedRow() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = idList.get(row); // Get actual ID
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this faculty?", "Confirm", JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {
            String sql = "DELETE FROM faculty_details WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Faculty deleted successfully.");
            loadData();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateSelectedRow() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        int id = idList.get(row); // Get actual ID

        JTextField nameField = new JTextField((String) model.getValueAt(row, 1));
        JTextField dobField = new JTextField(model.getValueAt(row, 2).toString());
        JTextField emailField = new JTextField((String) model.getValueAt(row, 3));
        JTextField phoneField = new JTextField((String) model.getValueAt(row, 4));
        JTextField deptField = new JTextField((String) model.getValueAt(row, 5));
        JTextField qualField = new JTextField((String) model.getValueAt(row, 6));
        JTextField expField = new JTextField((String) model.getValueAt(row, 7));
        JTextField addressField = new JTextField((String) model.getValueAt(row, 8));

        JPanel panel = new JPanel(new GridLayout(9,2,5,5));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("DOB:")); panel.add(dobField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Department:")); panel.add(deptField);
        panel.add(new JLabel("Qualification:")); panel.add(qualField);
        panel.add(new JLabel("Experience:")); panel.add(expField);
        panel.add(new JLabel("Address:")); panel.add(addressField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Update Faculty", JOptionPane.OK_CANCEL_OPTION);
        if(option != JOptionPane.OK_OPTION) return;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {
            String sql = "UPDATE faculty_details SET emp_name=?, dob=?, emp_email=?, emp_ph=?, emp_dept=?, emp_qua=?, emp_exp=?, address=? WHERE id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, nameField.getText());
            pst.setString(2, dobField.getText());
            pst.setString(3, emailField.getText());
            pst.setString(4, phoneField.getText());
            pst.setString(5, deptField.getText());
            pst.setString(6, qualField.getText());
            pst.setString(7, expField.getText());
            pst.setString(8, addressField.getText());
            pst.setInt(9, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Faculty updated successfully!");
            loadData();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void searchFaculty() {
        String input = JOptionPane.showInputDialog(this, "Enter Faculty ID or Name:");
        if(input == null || input.trim().isEmpty()) return;

        try {
            model.setRowCount(0);
            idList.clear();

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
            String sql = "SELECT * FROM faculty_details WHERE id=? OR emp_name LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            try { pst.setInt(1, Integer.parseInt(input)); }
            catch(NumberFormatException e) { pst.setInt(1, -1); }
            pst.setString(2, "%" + input + "%");

            ResultSet rs = pst.executeQuery();
            int serial = 1;
            while(rs.next()) {
                int id = rs.getInt("id");
                idList.add(id);
                Object[] row = {
                    serial++,
                    rs.getString("emp_name"),
                    rs.getDate("dob"),
                    rs.getString("emp_email"),
                    rs.getString("emp_ph"),
                    rs.getString("emp_dept"),
                    rs.getString("emp_qua"),
                    rs.getString("emp_exp"),
                    rs.getString("address")
                };
                model.addRow(row);
            }
            con.close();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
