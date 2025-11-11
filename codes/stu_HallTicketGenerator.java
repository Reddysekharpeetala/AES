import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;

public class stu_HallTicketGenerator extends JPanel implements ActionListener {

    private JComboBox<String> comboHallTickets;
    private JLabel lblName, lblHallTicket, lblProgram, lblSemester;
    private JTable tableSubjects;
    private JTextArea txtInstructions;
    private JButton btnGenerateHallTicket;
    private Connection con;

    private String currentHallTicket = "";
    private String currentName = "";
    private String currentProgram = "";
    private String currentSemester = "";
    private String currentQualification = "";

    // Default constructor
    public stu_HallTicketGenerator() {
        initComponents();
        connectDB();
        loadHallTickets();
    }

    // Constructor with hall ticket
    public stu_HallTicketGenerator(String hallTicket) {
        this();
        comboHallTickets.setSelectedItem(hallTicket);
        loadStudentData(hallTicket);
        comboHallTickets.setEnabled(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        comboHallTickets = new JComboBox<>();
        comboHallTickets.setPreferredSize(new Dimension(200, 25));
        comboHallTickets.addActionListener(this);
        topPanel.add(new JLabel("Select Hall Ticket: "));
        topPanel.add(comboHallTickets);
        add(topPanel, BorderLayout.NORTH);

        // Main panel
        JPanel hallTicketPanel = new JPanel();
        hallTicketPanel.setLayout(new BoxLayout(hallTicketPanel, BoxLayout.Y_AXIS));
        hallTicketPanel.setBackground(Color.WHITE);
        hallTicketPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Heading
        JLabel collegeName = new JLabel("Shri Gnanambica Degree College, Madanapalle", SwingConstants.CENTER);
        collegeName.setFont(new Font("Serif", Font.BOLD, 20));
        collegeName.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel hallTicketHeading = new JLabel("HALL TICKET", SwingConstants.CENTER);
        hallTicketHeading.setFont(new Font("Serif", Font.BOLD, 24));
        hallTicketHeading.setForeground(new Color(0, 102, 204));
        hallTicketHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        hallTicketPanel.add(collegeName);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        hallTicketPanel.add(hallTicketHeading);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 15, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(700, 50));
        lblName = new JLabel("Name: ");
        lblHallTicket = new JLabel("Hall Ticket No: ");
        lblProgram = new JLabel("Program: ");
        lblSemester = new JLabel("Semester: ");
        Font lblFont = new Font("Arial", Font.BOLD, 14);
        lblName.setFont(lblFont);
        lblHallTicket.setFont(lblFont);
        lblProgram.setFont(lblFont);
        lblSemester.setFont(lblFont);

        infoPanel.add(lblName);
        infoPanel.add(lblHallTicket);
        infoPanel.add(lblProgram);
        infoPanel.add(lblSemester);
        hallTicketPanel.add(infoPanel);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Table panel
        String[] cols = {"Seat No", "Date", "Timings", "Subject"};
        tableSubjects = new JTable(new DefaultTableModel(cols, 0));
        tableSubjects.setFillsViewportHeight(true);
        tableSubjects.setFont(new Font("Arial", Font.PLAIN, 14));
        tableSubjects.setRowHeight(25);
        tableSubjects.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tableSubjects.setBorder(new LineBorder(Color.BLACK, 1, true));
        JScrollPane sp = new JScrollPane(tableSubjects);
        sp.setPreferredSize(new Dimension(750, 300));
        hallTicketPanel.add(sp);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Instructions
        txtInstructions = new JTextArea(
                "Instructions:\n1. Carry valid ID.\n2. Reach 30 mins early.\n3. No electronic devices.\n4. Follow rules."
        );
        txtInstructions.setEditable(false);
        txtInstructions.setLineWrap(true);
        txtInstructions.setWrapStyleWord(true);
        txtInstructions.setBackground(new Color(245, 245, 245));
        txtInstructions.setBorder(BorderFactory.createTitledBorder("Candidate Instructions"));
        txtInstructions.setFont(new Font("Arial", Font.PLAIN, 14));
        txtInstructions.setPreferredSize(new Dimension(750, 120));
        hallTicketPanel.add(txtInstructions);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        btnGenerateHallTicket = new JButton("Download Hall Ticket");
        btnGenerateHallTicket.setFont(new Font("Arial", Font.BOLD, 16));
        btnGenerateHallTicket.setBackground(new Color(0, 102, 204));
        btnGenerateHallTicket.setForeground(Color.WHITE);
        btnGenerateHallTicket.setFocusPainted(false);
        btnGenerateHallTicket.setPreferredSize(new Dimension(200, 40));
        btnGenerateHallTicket.addActionListener(this);
        bottomPanel.add(btnGenerateHallTicket);
        hallTicketPanel.add(bottomPanel);

        add(hallTicketPanel, BorderLayout.CENTER);
    }

    private void connectDB() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadHallTickets() {
        try {
            comboHallTickets.addItem("Select Hall Ticket");
            String sql = "SELECT hall_ticket FROM halltickets WHERE hall_ticket IN " +
                    "(SELECT hallticket_no FROM exam_fee WHERE status='PAID')";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                comboHallTickets.addItem(rs.getString("hall_ticket"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadStudentData(String hallTicketNo) {
        try {
            String sql = "SELECT s.first_name, s.last_name, s.prev_qualification, h.program_name " +
                    "FROM student_details s JOIN halltickets h ON s.id = h.stu_id " +
                    "WHERE h.hall_ticket = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, hallTicketNo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                currentName = rs.getString("first_name") + " " + rs.getString("last_name");
                currentHallTicket = hallTicketNo;
                currentProgram = rs.getString("program_name");
                currentQualification = rs.getString("prev_qualification");

                lblName.setText("Name: " + currentName);
                lblHallTicket.setText("Hall Ticket No: " + currentHallTicket);
                lblProgram.setText("Program: " + currentProgram);

                currentSemester = getFirstSemester(currentProgram);
                lblSemester.setText("Semester: " + currentSemester);

                loadExamTimetable(currentProgram, currentSemester, currentQualification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getFirstSemester(String program) {
        try {
            String sql = "SELECT DISTINCT semester FROM subjects WHERE program_name = ? ORDER BY semester ASC LIMIT 1";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, program);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("semester");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void loadExamTimetable(String program, String semester, String qualification) {
        try {
            DefaultTableModel model = (DefaultTableModel) tableSubjects.getModel();
            model.setRowCount(0);
            String seatNo = "S" + currentHallTicket.substring(Math.max(currentHallTicket.length() - 4, 0));

            String sql = "SELECT s.subject_name, e.exam_date, e.start_time, e.end_time " +
                    "FROM subjects s LEFT JOIN exam_timetable e " +
                    "ON s.subject_id = e.course_code AND s.program_name = e.program AND e.semester = ? " +
                    "WHERE s.program_name = ? ORDER BY e.exam_date, e.start_time";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, semester);
            pst.setString(2, program);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String subject = rs.getString("subject_name");
                String date = rs.getString("exam_date") != null ? rs.getString("exam_date") : "";
                String timings = "";
                if (rs.getString("start_time") != null && rs.getString("end_time") != null) {
                    timings = rs.getString("start_time") + " - " + rs.getString("end_time");
                }
                model.addRow(new Object[]{seatNo, date, timings, subject});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comboHallTickets) {
            String hallTicketNo = (String) comboHallTickets.getSelectedItem();
            if (hallTicketNo != null && !hallTicketNo.equals("Select Hall Ticket")) {
                loadStudentData(hallTicketNo);
            }
        } else if (e.getSource() == btnGenerateHallTicket) {
            saveHallTicketAsPNG();
        }
    }

    private void saveHallTicketAsPNG() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(currentHallTicket + "_HallTicket.png"));
            int retrival = chooser.showSaveDialog(this);
            if (retrival == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                this.paint(g2);
                g2.dispose();
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "Hall Ticket saved as PNG successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving Hall Ticket: " + ex.getMessage());
        }
    }
}
