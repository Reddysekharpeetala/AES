import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class view_Results extends JPanel {

    JLabel lad, lhn, lname, lgro, ltm;
    JTextField hn, name, gro, tm;
    JTable resultTable;
    DefaultTableModel tableModel;
    GridBagConstraints gc;
    private Connection con;
    private String hallTicket; // Logged-in hall ticket

    public view_Results(String loggedHallTicket) {
        this.hallTicket = loggedHallTicket;

        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.BOTH;

        connectDatabase();

        lad = new JLabel("AGGREGATE DETAILS:");
        lhn = new JLabel("HALL TICKET NO");
        lname = new JLabel("NAME");
        lgro = new JLabel("PROGRAM");
        ltm = new JLabel("TOTAL MARKS");

        hn = new JTextField(10);
        hn.setText(hallTicket);
        hn.setEditable(false); // Hall Ticket is not editable
        name = new JTextField(40);
        name.setEditable(false);
        gro = new JTextField(20);
        gro.setEditable(false);
        tm = new JTextField(10);
        tm.setEditable(false);

        // Table columns
        String[] columns = {"S.No", "Subject", "Marks", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(600, 250));

        // Add components
        addC(lad, 1, 1, 5, 1);
        addC(lhn, 2, 1, 1, 1); addC(hn, 2, 2, 2, 1);
        addC(lname, 3, 1, 1, 1); addC(name, 3, 2, 3, 1);
        addC(lgro, 4, 1, 1, 1); addC(gro, 4, 2, 3, 1);
        addC(ltm, 5, 1, 1, 1); addC(tm, 5, 2, 1, 1);
        addC(scrollPane, 6, 1, 5, 5);

        // Fonts
        lad.setFont(new Font("Arial", Font.BOLD, 25));
        lhn.setFont(new Font("Arial", Font.BOLD, 16));
        lname.setFont(new Font("Arial", Font.BOLD, 16));
        lgro.setFont(new Font("Arial", Font.BOLD, 16));
        ltm.setFont(new Font("Arial", Font.BOLD, 16));

        // Automatically load student result
        searchResult();
    }

    private void addC(Component cc, int r, int c, int w, int h){
        gc.gridx = c;
        gc.gridy = r;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.fill = GridBagConstraints.BOTH;
        add(cc, gc);
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan",
                    "root", "reddy123");
            con.setAutoCommit(true);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchResult() {
        try {
            tableModel.setRowCount(0);
            name.setText("");
            gro.setText("");
            tm.setText("");

            // Step 1 – Get stu_id and program_name
            PreparedStatement pst1 = con.prepareStatement(
                "SELECT stu_id, program_name FROM halltickets WHERE hall_ticket = ?");
            pst1.setString(1, hallTicket);
            ResultSet rs1 = pst1.executeQuery();

            if (!rs1.next()) {
                JOptionPane.showMessageDialog(this, "No student found for this Hall Ticket", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int stuId = rs1.getInt("stu_id");
            String programName = rs1.getString("program_name");
            gro.setText(programName);

            // Step 2 – Check attendance
            PreparedStatement pstAttend = con.prepareStatement(
                "SELECT COUNT(*) AS present_count FROM exam_attendance WHERE student_id=? AND attendance_status='Yes'");
            pstAttend.setInt(1, stuId);
            ResultSet rsAttend = pstAttend.executeQuery();

            if (rsAttend.next() && rsAttend.getInt("present_count") == 0) {
                JOptionPane.showMessageDialog(this, "Student has not attended any exam. Result not available.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Step 3 – Get student name
            PreparedStatement pst2 = con.prepareStatement(
                "SELECT CONCAT(first_name,' ',last_name) AS student_name FROM student_details WHERE id = ?");
            pst2.setInt(1, stuId);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                name.setText(rs2.getString("student_name"));
            }

            // Step 4 – Get exam marks
            PreparedStatement pst3 = con.prepareStatement(
                "SELECT exam_name, marks FROM exam_marks WHERE hall_ticket_no = ?");
            pst3.setString(1, hallTicket);
            ResultSet rs3 = pst3.executeQuery();

            int total = 0, sno = 1;
            boolean found = false;
            while (rs3.next()) {
                found = true;
                String subject = rs3.getString("exam_name");
                int mark = rs3.getInt("marks");
                total += mark;
                String grade = mark >= 28 ? "Pass" : "Fail";
                tableModel.addRow(new Object[]{sno++, subject, mark, grade});
            }

            if (found) tm.setText(String.valueOf(total));
            else JOptionPane.showMessageDialog(this, "No exam marks found for this student", "Info", JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
