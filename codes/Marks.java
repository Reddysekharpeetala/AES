import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Marks extends JPanel implements ActionListener, ItemListener {

    private JComboBox<String> programCombo, examCombo;
    private JTextField dateField;
    private JTable marksTable;
    private DefaultTableModel model;
    private JButton addMarksBtn, saveMarksBtn;
    private Connection con;

    public Marks() {
        setLayout(new BorderLayout());
        connectDatabase();

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Program:"));
        programCombo = new JComboBox<>();
        programCombo.addItemListener(this);
        topPanel.add(programCombo);

        topPanel.add(new JLabel("Select Exam:"));
        examCombo = new JComboBox<>();
        examCombo.addItemListener(this);
        topPanel.add(examCombo);

        topPanel.add(new JLabel("Exam Date:"));
        dateField = new JTextField(10);
        dateField.setEditable(false);
        topPanel.add(dateField);

        addMarksBtn = new JButton("Add Marks");
        addMarksBtn.addActionListener(this);
        topPanel.add(addMarksBtn);

        saveMarksBtn = new JButton("Save Marks");
        saveMarksBtn.addActionListener(this);
        topPanel.add(saveMarksBtn);

        add(topPanel, BorderLayout.NORTH);

        // Marks Table
        model = new DefaultTableModel(new Object[]{"S.No", "Student Name", "Hall Ticket No", "Marks"}, 0);
        marksTable = new JTable(model);
        add(new JScrollPane(marksTable), BorderLayout.CENTER);

        loadPrograms();
    }

    private void connectDatabase() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed");
        }
    }

    private void loadPrograms() {
        try {
            String sql = "SELECT DISTINCT program FROM exam_timetable";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            programCombo.removeAllItems();
            while (rs.next()) {
                programCombo.addItem(rs.getString("program"));
            }
            if (programCombo.getItemCount() > 0) programCombo.setSelectedIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExamsForSelectedProgram() {
        try {
            if (programCombo.getSelectedItem() == null) return;
            String program = programCombo.getSelectedItem().toString();

            String sql = "SELECT DISTINCT course_name, exam_date FROM exam_timetable WHERE program=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, program);
            ResultSet rs = pst.executeQuery();

            examCombo.removeAllItems();
            while (rs.next()) {
                examCombo.addItem(rs.getString("course_name"));
                dateField.setText(rs.getDate("exam_date").toString());
            }
            if (examCombo.getItemCount() > 0) examCombo.setSelectedIndex(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadMarksTable();
    }

    private void updateExamDate() {
        try {
            if (programCombo.getSelectedItem() == null || examCombo.getSelectedItem() == null) {
                dateField.setText("");
                return;
            }
            String program = programCombo.getSelectedItem().toString();
            String examName = examCombo.getSelectedItem().toString();

            String sql = "SELECT exam_date FROM exam_timetable WHERE program=? AND course_name=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, program);
            pst.setString(2, examName);
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

    private void loadMarksTable() {
        try {
            model.setRowCount(0);
            if (programCombo.getSelectedItem() == null || examCombo.getSelectedItem() == null) return;

            String program = programCombo.getSelectedItem().toString();
            String examName = examCombo.getSelectedItem().toString();

            // Only include students who are present in exam_attendance
            String sql = "SELECT sd.first_name, sd.last_name, h.hall_ticket, IFNULL(em.marks,0) AS marks " +
                    "FROM student_details sd " +
                    "JOIN halltickets h ON sd.id = h.stu_id " +
                    "JOIN exam_attendance ea ON sd.id = ea.student_id " +
                    "LEFT JOIN exam_marks em ON em.student_name = CONCAT(sd.first_name,' ',sd.last_name) " +
                    "AND em.program = ? AND em.exam_name = ? " +
                    "WHERE h.program_name = ? AND ea.exam_id = (SELECT exam_id FROM exam_timetable WHERE program=? AND course_name=?) " +
                    "AND ea.attendance_status='Yes'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, program);
            pst.setString(2, examName);
            pst.setString(3, program);
            pst.setString(4, program);
            pst.setString(5, examName);
            ResultSet rs = pst.executeQuery();

            int sno = 1;
            while (rs.next()) {
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String hall = rs.getString("hall_ticket");
                int marks = rs.getInt("marks");
                model.addRow(new Object[]{sno++, name, hall, marks});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading marks table.");
        }
    }

    private void addMarks() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No present students available for this exam.");
            return;
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            String studentName = model.getValueAt(i, 1).toString();
            String hallTicket = model.getValueAt(i, 2).toString();

            String marksStr = JOptionPane.showInputDialog(this,
                    "Enter marks for " + studentName + " (" + hallTicket + "):",
                    model.getValueAt(i, 3));

            if (marksStr == null) continue;

            try {
                int marks = Integer.parseInt(marksStr);
                if (marks < 0 || marks > 100) throw new NumberFormatException();
                model.setValueAt(marks, i, 3);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid marks for " + studentName + ". Must be 0-100.");
                i--; // repeat this student
            }
        }
    }

    private void saveMarks() {
        try {
            if (programCombo.getSelectedItem() == null || examCombo.getSelectedItem() == null) return;

            String program = programCombo.getSelectedItem().toString();
            String examName = examCombo.getSelectedItem().toString();
            String examDate = dateField.getText();

            for (int i = 0; i < model.getRowCount(); i++) {
                String studentName = model.getValueAt(i, 1).toString();
                String hallTicket = model.getValueAt(i, 2).toString();
                int marks = Integer.parseInt(model.getValueAt(i, 3).toString());

                // Check if record exists
                String checkSql = "SELECT id FROM exam_marks WHERE student_name=? AND program=? AND exam_name=?";
                PreparedStatement pstCheck = con.prepareStatement(checkSql);
                pstCheck.setString(1, studentName);
                pstCheck.setString(2, program);
                pstCheck.setString(3, examName);
                ResultSet rsCheck = pstCheck.executeQuery();

                if (rsCheck.next()) {
                    // UPDATE
                    String updateSql = "UPDATE exam_marks SET marks=? WHERE id=?";
                    PreparedStatement pstUpdate = con.prepareStatement(updateSql);
                    pstUpdate.setInt(1, marks);
                    pstUpdate.setInt(2, rsCheck.getInt("id"));
                    pstUpdate.executeUpdate();
                } else {
                    // INSERT
                    String insertSql = "INSERT INTO exam_marks(program, exam_name, exam_date, student_name, hall_ticket_no, marks) VALUES(?,?,?,?,?,?)";
                    PreparedStatement pstInsert = con.prepareStatement(insertSql);
                    pstInsert.setString(1, program);
                    pstInsert.setString(2, examName);
                    pstInsert.setDate(3, java.sql.Date.valueOf(examDate));
                    pstInsert.setString(4, studentName);
                    pstInsert.setString(5, hallTicket);
                    pstInsert.setInt(6, marks);
                    pstInsert.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(this, "Marks saved successfully!");
            loadMarksTable();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving marks.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addMarksBtn) addMarks();
        else if (e.getSource() == saveMarksBtn) saveMarks();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource() == programCombo) loadExamsForSelectedProgram();
            else if (e.getSource() == examCombo) {
                updateExamDate();
                loadMarksTable();
            }
        }
    }
}
