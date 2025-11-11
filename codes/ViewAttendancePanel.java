import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewAttendancePanel extends JPanel implements ActionListener, ItemListener {

    private JComboBox<String> groupCombo, examCombo;
    private JTextField dateField;
    private JTable attendanceTable;
    private DefaultTableModel model;
    private JButton takeBtn, uploadBtn;
    private Connection con;

    public ViewAttendancePanel() {
        setLayout(new BorderLayout());
        connectDatabase();

        // ---------------- Top Panel ----------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Select Group:"));
        groupCombo = new JComboBox<>();
        loadGroups();
        topPanel.add(groupCombo);

        topPanel.add(new JLabel("Select Exam:"));
        examCombo = new JComboBox<>();
        topPanel.add(examCombo);

        topPanel.add(new JLabel("Exam Date:"));
        dateField = new JTextField(10);
        dateField.setEditable(false);
        topPanel.add(dateField);

        takeBtn = new JButton("Take Attendance");
        takeBtn.addActionListener(this);
        topPanel.add(takeBtn);

        uploadBtn = new JButton("Upload Attendance");
        uploadBtn.addActionListener(this);
        topPanel.add(uploadBtn);

        add(topPanel, BorderLayout.NORTH);

        // ---------------- Table ----------------
        model = new DefaultTableModel(new Object[]{"S.No", "Student Name", "Hall Ticket No", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Status editable
            }
        };
        attendanceTable = new JTable(model);

        // Restrict Status column to only "Yes" or "No"
        String[] statuses = {"Yes", "No"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        attendanceTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statusCombo));

        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        // ---------------- Listeners ----------------
        groupCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadExamsForGroup(groupCombo.getSelectedItem().toString());
            }
        });

        examCombo.addItemListener(this);
    }

    // ---------------- Database Connection ----------------
    private void connectDatabase() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed");
        }
    }

    // ---------------- Load Groups ----------------
    private void loadGroups() {
        try {
            String sql = "SELECT DISTINCT program_name FROM programs";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            groupCombo.removeAllItems();
            while (rs.next()) {
                groupCombo.addItem(rs.getString("program_name"));
            }
            if (groupCombo.getItemCount() > 0) {
                groupCombo.setSelectedIndex(0); // auto select first group
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- Load Exams ----------------
    private void loadExamsForGroup(String group) {
        try {
            String sql = "SELECT exam_id, course_name FROM exam_timetable WHERE program=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, group);
            ResultSet rs = pst.executeQuery();
            examCombo.removeAllItems();
            while (rs.next()) {
                examCombo.addItem(rs.getInt("exam_id") + " - " + rs.getString("course_name"));
            }
            if (examCombo.getItemCount() > 0) {
                examCombo.setSelectedIndex(0); // auto select first exam
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- Update Exam Date ----------------
    private void updateExamDate() {
        try {
            if (examCombo.getSelectedItem() == null) {
                dateField.setText("");
                return;
            }
            int examId = Integer.parseInt(examCombo.getSelectedItem().toString().split(" - ")[0]);
            String sql = "SELECT exam_date FROM exam_timetable WHERE exam_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, examId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                dateField.setText(rs.getDate("exam_date").toString());
            } else {
                dateField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dateField.setText("");
        }
    }

    // ---------------- Load Attendance ----------------
    private void loadAttendance() {
        try {
            model.setRowCount(0); // Clear table
            if (examCombo.getSelectedItem() == null || groupCombo.getSelectedItem() == null) return;

            String group = groupCombo.getSelectedItem().toString();
            int examId = Integer.parseInt(examCombo.getSelectedItem().toString().split(" - ")[0]);

            String sql = "SELECT s.id, s.first_name, s.last_name, h.hall_ticket, ea.attendance_status " +
                    "FROM student_details s " +
                    "LEFT JOIN halltickets h ON s.id = h.stu_id AND h.program_name=? " +
                    "LEFT JOIN exam_attendance ea ON s.id = ea.student_id AND ea.exam_id=? " +
                    "WHERE s.course=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, group);
            pst.setInt(2, examId);
            pst.setString(3, group);
            ResultSet rs = pst.executeQuery();

            int sno = 1;
            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String hallTicket = rs.getString("hall_ticket");
                String status = rs.getString("attendance_status");
                if (status == null) status = "No";
                model.addRow(new Object[]{sno++, name, hallTicket, status});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------- Take Attendance ----------------
    private void takeAttendance() {
        int selectedRow = attendanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to take attendance");
            return;
        }
        model.setValueAt("Yes", selectedRow, 3);
    }

    // ---------------- Upload Attendance ----------------
    private void uploadAttendance() {
        try {
            if (examCombo.getSelectedItem() == null || groupCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select group and exam.");
                return;
            }

            int examId = Integer.parseInt(examCombo.getSelectedItem().toString().split(" - ")[0]);
            String group = groupCombo.getSelectedItem().toString();

            for (int i = 0; i < model.getRowCount(); i++) {
                String status = model.getValueAt(i, 3).toString().trim();
                if (!status.equalsIgnoreCase("Yes") && !status.equalsIgnoreCase("No")) {
                    JOptionPane.showMessageDialog(this, "Status must be 'Yes' or 'No' in all rows.");
                    return;
                }

                String hallTicket = model.getValueAt(i, 2).toString();
                String sqlId = "SELECT stu_id FROM halltickets WHERE hall_ticket=? AND program_name=?";
                PreparedStatement pstId = con.prepareStatement(sqlId);
                pstId.setString(1, hallTicket);
                pstId.setString(2, group);
                ResultSet rsId = pstId.executeQuery();
                int studentId = 0;
                if (rsId.next()) {
                    studentId = rsId.getInt("stu_id");
                }

                String sqlCheck = "SELECT * FROM exam_attendance WHERE exam_id=? AND student_id=?";
                PreparedStatement pstCheck = con.prepareStatement(sqlCheck);
                pstCheck.setInt(1, examId);
                pstCheck.setInt(2, studentId);
                ResultSet rsCheck = pstCheck.executeQuery();

                if (rsCheck.next()) {
                    String sqlUpdate = "UPDATE exam_attendance SET attendance_status=? WHERE exam_id=? AND student_id=?";
                    PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate);
                    pstUpdate.setString(1, status);
                    pstUpdate.setInt(2, examId);
                    pstUpdate.setInt(3, studentId);
                    pstUpdate.executeUpdate();
                } else {
                    String sqlInsert = "INSERT INTO exam_attendance(exam_id, student_id, attendance_status) VALUES(?,?,?)";
                    PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
                    pstInsert.setInt(1, examId);
                    pstInsert.setInt(2, studentId);
                    pstInsert.setString(3, status);
                    pstInsert.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Attendance uploaded successfully!");
            loadAttendance(); // Reload table

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error uploading attendance");
        }
    }

    // ---------------- ActionListener ----------------
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == takeBtn) {
            takeAttendance();
        } else if (e.getSource() == uploadBtn) {
            uploadAttendance();
        }
    }

    // ---------------- ItemListener ----------------
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == examCombo && e.getStateChange() == ItemEvent.SELECTED) {
            updateExamDate();
            loadAttendance();
        }
    }

    }
