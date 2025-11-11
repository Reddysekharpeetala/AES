import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class faculty_attendance extends JPanel implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnInsert, btnFetch;
    JTextField txtDate;

    public faculty_attendance() {
        setLayout(new BorderLayout(10,10));

        // Table columns
        String[] cols = {"Student ID", "First Name", "Last Name", "Course", "Status"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);

        // Status dropdown
        String[] statusOptions = {"Present", "Absent"};
        TableColumn statusColumn = table.getColumnModel().getColumn(4);
        JComboBox<String> comboBox = new JComboBox<>(statusOptions);
        statusColumn.setCellEditor(new DefaultCellEditor(comboBox));

        // Date input
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtDate = new JTextField(10);
        btnFetch = new JButton("Fetch Students");
        btnInsert = new JButton("Insert Attendance");

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(txtDate);
        form.add(btnFetch);
        form.add(btnInsert);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnFetch.addActionListener(this);
        btnInsert.addActionListener(this);
    }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFetch) {
            fetchStudents();
        } else if (e.getSource() == btnInsert) {
            insertAttendance();
        }
    }

    private void fetchStudents() {
        model.setRowCount(0); // clear table
        String query = "SELECT id, first_name, last_name, course FROM student_details";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("course"),
                        "absent" // default status
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching student data!");
        }
    }

    private void insertAttendance() {
        String dateStr = txtDate.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date is required!");
            return;
        }

        java.sql.Date sqlDate;
        try {
            sqlDate = java.sql.Date.valueOf(dateStr);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in format YYYY-MM-DD!");
            return;
        }

        try (Connection con = getConnection()) {
            String sql = "INSERT INTO attendance(student_id, course_id, att_date, status) VALUES (?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            for (int i = 0; i < model.getRowCount(); i++) {
                ps.setString(1, model.getValueAt(i, 0).toString()); // Student ID
                ps.setString(2, model.getValueAt(i, 3).toString()); // Course ID
                ps.setDate(3, sqlDate);
                ps.setString(4, model.getValueAt(i, 4).toString()); // Status
                ps.addBatch();
            }

            ps.executeBatch();
            JOptionPane.showMessageDialog(this, "Attendance inserted successfully!");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting attendance!");
        }
    }

  }










