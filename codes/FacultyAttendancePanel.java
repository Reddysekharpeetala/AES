import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FacultyAttendancePanel extends JPanel implements ActionListener {

    private JComboBox<String> examCombo;
    private JTable studentTable;
    private JButton submitBtn;
    private Connection con;
    private int facultyId; // Faculty ID passed from login
    private DefaultTableModel model;

    public FacultyAttendancePanel(int facultyId) {
        this.facultyId = facultyId;

        setLayout(new BorderLayout());

        // Connect to database
        connectDatabase();

        // Top panel with exam selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Exam:"));

        examCombo = new JComboBox<>();
        loadExams();
        topPanel.add(examCombo);

        add(topPanel, BorderLayout.NORTH);

        // Table for students
        model = new DefaultTableModel(new Object[]{"Student ID", "Name", "Status"}, 0);
        studentTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load students when exam is selected
        examCombo.addActionListener(e -> loadStudents());

        // Submit button
        submitBtn = new JButton("Submit Attendance");
        submitBtn.addActionListener(this);
        add(submitBtn, BorderLayout.SOUTH);

        // Initial load of students
        loadStudents();
    }

    private void connectDatabase() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/aes_db", "root", "password");
            System.out.println("Database Connected");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed");
        }
    }

    private void loadExams() {
        try {
            String sql = "SELECT exam_id, exam_name FROM exam_timetable";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            examCombo.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("exam_id");
                String name = rs.getString("exam_name");
                examCombo.addItem(id + " - " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStudents() {
        try {
            model.setRowCount(0); // clear existing rows
            if (examCombo.getSelectedItem() == null) return;

            int examId = Integer.parseInt(examCombo.getSelectedItem().toString().split(" - ")[0]);

            String sql = "SELECT s.id, s.first_name, s.last_name FROM student_details s " +
                         "JOIN exam_timetable e ON e.exam_id = ? " +
                         "JOIN programs p ON p.program_name = e.program_name " +
                         "JOIN student_details sd ON sd.program_name = p.program_name";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, examId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                model.addRow(new Object[]{studentId, name, "Present"});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            saveAttendance();
        }
    }

    private void saveAttendance() {
        try {
            if (examCombo.getSelectedItem() == null) return;
            int examId = Integer.parseInt(examCombo.getSelectedItem().toString().split(" - ")[0]);

            String sql = "INSERT INTO exam_attendance (exam_id, student_id, attendance_status, taken_by) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            for (int i = 0; i < model.getRowCount(); i++) {
                int studentId = (int) model.getValueAt(i, 0);
                String status = model.getValueAt(i, 2).toString();

                pst.setInt(1, examId);
                pst.setInt(2, studentId);
                pst.setString(3, status);
                pst.setInt(4, facultyId);

                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Attendance submitted successfully!");

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Attendance already submitted for some students.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving attendance.");
        }
    }
}
