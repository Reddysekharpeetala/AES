import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class view_exam_timetable extends JPanel implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private JButton searchBtn;
    private JComboBox<String> semesterCombo;

    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    private String loggedHallTicket;
    private String loggedProgram;

    public view_exam_timetable(String hallTicket, String program) {
        this.loggedHallTicket = hallTicket;
        this.loggedProgram = program;

        setLayout(new BorderLayout()); // Full panel
        setBackground(Color.WHITE);

        // --- Top panel with Program, Semester, Search button ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(220, 220, 220));

        topPanel.add(new JLabel("Program:"));
        JLabel lblProgram = new JLabel(program);
        lblProgram.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(lblProgram);

        topPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>();
        semesterCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(semesterCombo);

        searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));
        searchBtn.setBackground(new Color(0, 102, 204));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(this);
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- Table setup ---
        String[] columns = {"S.No", "Course Name", "Semester", "Exam Date", "Start Time", "End Time", "Venue"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Fit table to panel width
        table.setRowHeight(50);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));

        // Wrap text in Course Name column
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JTextArea area = new JTextArea(value != null ? value.toString() : "");
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setFont(new Font("Arial", Font.PLAIN, 16));
                if (isSelected) {
                    area.setBackground(table.getSelectionBackground());
                    area.setForeground(table.getSelectionForeground());
                } else {
                    area.setBackground(table.getBackground());
                    area.setForeground(table.getForeground());
                }
                return area;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER); // Fill remaining space

        loadSemesters();
        refreshTable();
    }

    private void connect() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
    }

    private void loadSemesters() {
        semesterCombo.removeAllItems();
        semesterCombo.addItem("All");
        try {
            connect();
            pst = con.prepareStatement("SELECT DISTINCT semester FROM subjects WHERE program_name=? ORDER BY semester");
            pst.setString(1, loggedProgram);
            rs = pst.executeQuery();
            while (rs.next()) {
                semesterCombo.addItem(rs.getString("semester"));
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading semesters: " + e.getMessage());
        }
    }

    public void refreshTable() {
        loadFilteredSubjects("All");
    }

    private void loadFilteredSubjects(String selectedSemester) {
        model.setRowCount(0);
        try {
            connect();
            String sql = "SELECT s.subject_name, s.semester, e.exam_date, e.start_time, e.end_time, e.venue " +
                    "FROM subjects s INNER JOIN exam_timetable e ON s.subject_id=e.course_code " +
                    "WHERE s.program_name=?";
            if (!selectedSemester.equals("All")) sql += " AND s.semester=?";

            pst = con.prepareStatement(sql);
            pst.setString(1, loggedProgram);
            if (!selectedSemester.equals("All")) pst.setString(2, selectedSemester);

            rs = pst.executeQuery();
            int rowNumber = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rowNumber++,
                        rs.getString("subject_name"),
                        rs.getString("semester"),
                        rs.getString("exam_date"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("venue")
                });
            }
            con.close();

            // Adjust row heights dynamically
            for (int row = 0; row < table.getRowCount(); row++) {
                int maxHeight = 50;
                for (int column = 0; column < table.getColumnCount(); column++) {
                    TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                    Component comp = table.prepareRenderer(cellRenderer, row, column);
                    maxHeight = Math.max(comp.getPreferredSize().height + 10, maxHeight);
                }
                table.setRowHeight(row, maxHeight);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            loadFilteredSubjects(semesterCombo.getSelectedItem().toString());
        }
    }
}
