import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class exam_timetable extends JPanel implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton addBtn, updateBtn, deleteBtn, searchBtn;
    private JComboBox<String> programCombo, semesterCombo;

    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    public exam_timetable() {
        setLayout(new BorderLayout());

        // Fonts
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font tableFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        programCombo = new JComboBox<>();
        programCombo.setFont(fieldFont);
        semesterCombo = new JComboBox<>();
        semesterCombo.setFont(fieldFont);

        JLabel programLabel = new JLabel("Program:");
        programLabel.setFont(labelFont);
        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setFont(labelFont);

        searchBtn = new JButton("Search");
        searchBtn.setFont(buttonFont);

        topPanel.add(programLabel);
        topPanel.add(programCombo);
        topPanel.add(semesterLabel);
        topPanel.add(semesterCombo);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columns = {
                "Subject ID", "Subject Name", "Program", "Semester",
                "Exam Date", "Start Time", "End Time", "Venue",
                "Exam Type", "Instructor", "Notes"
        };
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(tableFont);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(labelFont);

        // Hide Subject ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        addBtn = new JButton("Add Exam");
        addBtn.setFont(buttonFont);
        updateBtn = new JButton("Update");
        updateBtn.setFont(buttonFont);
        deleteBtn = new JButton("Delete");
        deleteBtn.setFont(buttonFont);

        bottomPanel.add(addBtn);
        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        addBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        searchBtn.addActionListener(this);

        // Load programs and semesters
        loadPrograms();
        loadSemesters();

        // Load table data
        refreshTable();
    }

    // Database connection
    private void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
    }

    // Load programs
    private void loadPrograms() {
        programCombo.removeAllItems();
        programCombo.addItem("All");
        try {
            connect();
            pst = con.prepareStatement("SELECT program_name FROM programs");
            rs = pst.executeQuery();
            while (rs.next()) {
                programCombo.addItem(rs.getString("program_name"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load semesters
    private void loadSemesters() {
        semesterCombo.removeAllItems();
        semesterCombo.addItem("All");
        try {
            connect();
            pst = con.prepareStatement("SELECT DISTINCT semester FROM subjects ORDER BY semester");
            rs = pst.executeQuery();
            while (rs.next()) {
                semesterCombo.addItem(rs.getString("semester"));
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh table with both subjects and minor subjects
    private void refreshTable() {
        model.setRowCount(0);
        try {
            connect();

            // Load major subjects with exam details
            String sql1 = "SELECT s.subject_id, s.subject_name, s.program_name, s.semester, " +
                    "e.exam_date, e.start_time, e.end_time, e.venue, e.exam_type, e.instructor, e.notes " +
                    "FROM subjects s LEFT JOIN exam_timetable e ON s.subject_id = e.course_code " +
                    "ORDER BY s.subject_name";
            pst = con.prepareStatement(sql1);
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("subject_id"),
                        rs.getString("subject_name"),
                        rs.getString("program_name"),
                        rs.getString("semester"),
                        valueOrEmpty(rs.getString("exam_date")),
                        valueOrEmpty(rs.getString("start_time")),
                        valueOrEmpty(rs.getString("end_time")),
                        valueOrEmpty(rs.getString("venue")),
                        valueOrEmpty(rs.getString("exam_type")),
                        valueOrEmpty(rs.getString("instructor")),
                        valueOrEmpty(rs.getString("notes"))
                });
            }

            // Load minor subjects without exam details
            String sql2 = "SELECT minor_id, subject_name, program_name, semester FROM minor_subjects ORDER BY subject_name";
            pst = con.prepareStatement(sql2);
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("minor_id"),
                        rs.getString("subject_name"),
                        rs.getString("program_name"),
                        valueOrEmpty(rs.getString("semester")),  // Assuming minor_subjects has semester column; else use "N/A"
                        "", "", "", "", "", "", ""   // Empty values for exam-related fields
                });
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Filter subjects and minor subjects by program and semester
    private void loadFilteredSubjects() {
        model.setRowCount(0);
        String prog = programCombo.getSelectedItem().toString().trim();
        String sem = semesterCombo.getSelectedItem().toString().trim();

        try {
            connect();

            // Filter major subjects
            StringBuilder sql1 = new StringBuilder(
                    "SELECT s.subject_id, s.subject_name, s.program_name, s.semester, " +
                            "e.exam_date, e.start_time, e.end_time, e.venue, e.exam_type, e.instructor, e.notes " +
                            "FROM subjects s LEFT JOIN exam_timetable e ON s.subject_id = e.course_code WHERE 1=1"
            );
            if (!prog.equalsIgnoreCase("All")) sql1.append(" AND s.program_name = ?");
            if (!sem.equalsIgnoreCase("All")) sql1.append(" AND s.semester = ?");
            pst = con.prepareStatement(sql1.toString());

            int index = 1;
            if (!prog.equalsIgnoreCase("All")) pst.setString(index++, prog);
            if (!sem.equalsIgnoreCase("All")) pst.setString(index++, sem);

            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("subject_id"),
                        rs.getString("subject_name"),
                        rs.getString("program_name"),
                        rs.getString("semester"),
                        valueOrEmpty(rs.getString("exam_date")),
                        valueOrEmpty(rs.getString("start_time")),
                        valueOrEmpty(rs.getString("end_time")),
                        valueOrEmpty(rs.getString("venue")),
                        valueOrEmpty(rs.getString("exam_type")),
                        valueOrEmpty(rs.getString("instructor")),
                        valueOrEmpty(rs.getString("notes"))
                });
            }

            // Filter minor subjects
            StringBuilder sql2 = new StringBuilder(
                    "SELECT minor_id, subject_name, program_name, semester FROM minor_subjects WHERE 1=1"
            );
            if (!prog.equalsIgnoreCase("All")) sql2.append(" AND program_name = ?");
            if (!sem.equalsIgnoreCase("All")) sql2.append(" AND semester = ?");
            pst = con.prepareStatement(sql2.toString());

            index = 1;
            if (!prog.equalsIgnoreCase("All")) pst.setString(index++, prog);
            if (!sem.equalsIgnoreCase("All")) pst.setString(index++, sem);

            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("minor_id"),
                        rs.getString("subject_name"),
                        rs.getString("program_name"),
                        valueOrEmpty(rs.getString("semester")),
                        "", "", "", "", "", "", ""
                });
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add exam for major subjects only
    private void addExam() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a subject to add exam.");
            return;
        }

        int subjectId = (int) table.getValueAt(row, 0);
        String subjectName = table.getValueAt(row, 1).toString();
        String program = table.getValueAt(row, 2).toString();
        String semester = table.getValueAt(row, 3).toString();

        try {
            connect();
            pst = con.prepareStatement("SELECT exam_id FROM exam_timetable WHERE course_code = ?");
            pst.setInt(1, subjectId);
            rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Exam already exists for this subject. Use Update.");
                con.close();
                return;
            }

            JTextField examDate = new JTextField();
            JTextField startTime = new JTextField();
            JTextField endTime = new JTextField();
            JTextField venue = new JTextField();
            JTextField examType = new JTextField();
            JTextField instructor = new JTextField();
            JTextField notes = new JTextField();

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.add(new JLabel("Exam Date (YYYY-MM-DD):")); panel.add(examDate);
            panel.add(new JLabel("Start Time (HH:MM:SS):")); panel.add(startTime);
            panel.add(new JLabel("End Time (HH:MM:SS):")); panel.add(endTime);
            panel.add(new JLabel("Venue:")); panel.add(venue);
            panel.add(new JLabel("Exam Type:")); panel.add(examType);
            panel.add(new JLabel("Instructor:")); panel.add(instructor);
            panel.add(new JLabel("Notes:")); panel.add(notes);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add Exam for " + subjectName, JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                pst = con.prepareStatement(
                        "INSERT INTO exam_timetable (course_code, course_name, program, semester, exam_date, start_time, end_time, venue, exam_type, instructor, notes) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                pst.setInt(1, subjectId);
                pst.setString(2, subjectName);
                pst.setString(3, program);
                pst.setString(4, semester);
                pst.setString(5, examDate.getText());
                pst.setString(6, startTime.getText());
                pst.setString(7, endTime.getText());
                pst.setString(8, venue.getText());
                pst.setString(9, examType.getText());
                pst.setString(10, instructor.getText());
                pst.setString(11, notes.getText());

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Exam added successfully!");
                refreshTable();
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding exam: " + e.getMessage());
        }
    }

    // Update exam
    private void updateExam() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a subject to update exam.");
            return;
        }

        int subjectId = (int) table.getValueAt(row, 0);

        try {
            connect();
            pst = con.prepareStatement("SELECT * FROM exam_timetable WHERE course_code = ?");
            pst.setInt(1, subjectId);
            rs = pst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No exam scheduled for this subject.");
                con.close();
                return;
            }

            int examId = rs.getInt("exam_id");

            JTextField examDate = new JTextField(valueOrEmpty(rs.getString("exam_date")));
            JTextField startTime = new JTextField(valueOrEmpty(rs.getString("start_time")));
            JTextField endTime = new JTextField(valueOrEmpty(rs.getString("end_time")));
            JTextField venue = new JTextField(valueOrEmpty(rs.getString("venue")));
            JTextField examType = new JTextField(valueOrEmpty(rs.getString("exam_type")));
            JTextField instructor = new JTextField(valueOrEmpty(rs.getString("instructor")));
            JTextField notes = new JTextField(valueOrEmpty(rs.getString("notes")));

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.add(new JLabel("Exam Date:")); panel.add(examDate);
            panel.add(new JLabel("Start Time:")); panel.add(startTime);
            panel.add(new JLabel("End Time:")); panel.add(endTime);
            panel.add(new JLabel("Venue:")); panel.add(venue);
            panel.add(new JLabel("Exam Type:")); panel.add(examType);
            panel.add(new JLabel("Instructor:")); panel.add(instructor);
            panel.add(new JLabel("Notes:")); panel.add(notes);

            int confirm = JOptionPane.showConfirmDialog(this, panel, "Update Exam", JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION) {
                pst = con.prepareStatement(
                        "UPDATE exam_timetable SET exam_date=?, start_time=?, end_time=?, venue=?, exam_type=?, instructor=?, notes=? WHERE exam_id=?"
                );
                pst.setString(1, examDate.getText());
                pst.setString(2, startTime.getText());
                pst.setString(3, endTime.getText());
                pst.setString(4, venue.getText());
                pst.setString(5, examType.getText());
                pst.setString(6, instructor.getText());
                pst.setString(7, notes.getText());
                pst.setInt(8, examId);

                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Exam updated successfully!");
                refreshTable();
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating exam: " + e.getMessage());
        }
    }

    // Delete exam
    private void deleteExam() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a subject to delete exam.");
            return;
        }

        int subjectId = (int) table.getValueAt(row, 0);

        try {
            connect();
            pst = con.prepareStatement("SELECT exam_id FROM exam_timetable WHERE course_code=?");
            pst.setInt(1, subjectId);
            rs = pst.executeQuery();

            if (rs.next()) {
                int examId = rs.getInt("exam_id");
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this exam?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    pst = con.prepareStatement("DELETE FROM exam_timetable WHERE exam_id=?");
                    pst.setInt(1, examId);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Exam deleted successfully!");
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No exam found to delete for selected subject.");
            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting exam: " + e.getMessage());
        }
    }

    // Utility method
    private static String valueOrEmpty(Object obj) {
        return (obj == null) ? "" : obj.toString();
    }

    // Action handler
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) addExam();
        else if (e.getSource() == updateBtn) updateExam();
        else if (e.getSource() == deleteBtn) deleteExam();
        else if (e.getSource() == searchBtn) loadFilteredSubjects();
    }
}
