import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class HallTicketGenerator extends JPanel implements ActionListener {

    private JComboBox<String> comboHallTickets;
    private JLabel lblSelectTicket, lblName, lblHallTicket, lblProgram, lblSemester;
    private JTable tableSubjects;
    private JTextArea txtInstructions;
    private JButton btnGenerateHallTicket, btnDownloadAll;
    private Connection con;

    private String currentHallTicket = "";
    private String currentName = "";
    private String currentProgram = "";
    private String currentSemester = "";
    private String currentQualification = "";

    public HallTicketGenerator() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        connectDB();

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        lblSelectTicket = new JLabel("Select Hall Ticket: ");
        comboHallTickets = new JComboBox<>();
        comboHallTickets.addActionListener(this);
        topPanel.add(lblSelectTicket);
        topPanel.add(comboHallTickets);
        add(topPanel, BorderLayout.NORTH);

        // Hall Ticket Panel
        JPanel hallTicketPanel = new JPanel();
        hallTicketPanel.setLayout(new BoxLayout(hallTicketPanel, BoxLayout.Y_AXIS));
        hallTicketPanel.setBackground(Color.WHITE);

        // Heading Panel
        JPanel headingPanel = new JPanel();
        headingPanel.setLayout(new BoxLayout(headingPanel, BoxLayout.Y_AXIS));
        headingPanel.setBackground(Color.WHITE);

        JLabel collegeName = new JLabel("Shri Gnanambica Degree College, Madanapalle", SwingConstants.CENTER);
        collegeName.setFont(new Font("Serif", Font.BOLD, 18));
        collegeName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel autonomous = new JLabel("(AUTONOMOUS)", SwingConstants.CENTER);
        autonomous.setFont(new Font("Serif", Font.PLAIN, 16));
        autonomous.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel degreeExam = new JLabel("Degree Examinations", SwingConstants.CENTER);
        degreeExam.setFont(new Font("Serif", Font.PLAIN, 16));
        degreeExam.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel examType = new JLabel("Regular and Supplementary Examinations - March, 2026", SwingConstants.CENTER);
        examType.setFont(new Font("Serif", Font.PLAIN, 16));
        examType.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hallTicketHeading = new JLabel("HALL TICKET", SwingConstants.CENTER);
        hallTicketHeading.setFont(new Font("Serif", Font.BOLD, 20));
        hallTicketHeading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel line = new JLabel("---------------------", SwingConstants.CENTER);
        line.setFont(new Font("Serif", Font.PLAIN, 16));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);

        headingPanel.add(collegeName);
        headingPanel.add(autonomous);
        headingPanel.add(degreeExam);
        headingPanel.add(examType);
        headingPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headingPanel.add(hallTicketHeading);
        headingPanel.add(line);
        headingPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        hallTicketPanel.add(headingPanel);

        // Info Panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(Color.WHITE);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setBackground(Color.WHITE);

        lblName = new JLabel("Name: ");
        lblHallTicket = new JLabel("Hall Ticket No: ");
        lblProgram = new JLabel("Program: ");
        lblSemester = new JLabel("Semester: ");

        lblName.setFont(new Font("Serif", Font.PLAIN, 16));
        lblHallTicket.setFont(new Font("Serif", Font.PLAIN, 16));
        lblProgram.setFont(new Font("Serif", Font.PLAIN, 16));
        lblSemester.setFont(new Font("Serif", Font.PLAIN, 16));

        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblHallTicket.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblProgram.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSemester.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelPanel.add(lblName);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        labelPanel.add(lblHallTicket);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        labelPanel.add(lblProgram);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        labelPanel.add(lblSemester);

        infoPanel.add(labelPanel);
        hallTicketPanel.add(infoPanel);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Table Panel
        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tablePanel.setBackground(Color.WHITE);
        String[] cols = {"Seat No", "Date", "Timings", "Subject"};
        tableSubjects = new JTable(new DefaultTableModel(cols, 0));
        tableSubjects.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(tableSubjects);
        sp.setPreferredSize(new Dimension(750, 350));
        tablePanel.add(sp);
        hallTicketPanel.add(tablePanel);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Instructions Panel
        txtInstructions = new JTextArea(
                "Instructions to Students:\n" +
                        "1. Carry a valid ID proof along with this hall ticket.\n" +
                        "2. Reach the exam hall 30 minutes before the exam.\n" +
                        "3. Electronic devices are strictly prohibited.\n" +
                        "4. Follow all exam rules and regulations."
        );
        txtInstructions.setEditable(false);
        txtInstructions.setLineWrap(true);
        txtInstructions.setWrapStyleWord(true);
        txtInstructions.setBackground(Color.WHITE);
        txtInstructions.setBorder(BorderFactory.createTitledBorder("Candidate Instructions"));
        txtInstructions.setPreferredSize(new Dimension(750, 110));

        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        instructionsPanel.setBackground(Color.WHITE);
        instructionsPanel.add(txtInstructions);

        hallTicketPanel.add(instructionsPanel);
        hallTicketPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        add(hallTicketPanel, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        btnGenerateHallTicket = new JButton("Download Hall Ticket");
        btnGenerateHallTicket.addActionListener(this);
        btnDownloadAll = new JButton("Download All Hall Tickets");
        btnDownloadAll.addActionListener(this);
        bottomPanel.add(btnGenerateHallTicket);
        bottomPanel.add(btnDownloadAll);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load hall tickets automatically
        loadHallTickets();
    }

    private void connectDB() {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!");
        }
    }

    // ------------------ Auto-load only PAID hall tickets ------------------
    private void loadHallTickets() {
        try {
            comboHallTickets.removeAllItems();

            String sql = "SELECT h.hall_ticket " +
                         "FROM halltickets h " +
                         "JOIN exam_fee e ON h.hall_ticket = e.hallticket_no " +
                         "WHERE UPPER(TRIM(e.status))='PAID' " +
                         "ORDER BY h.hall_ticket ASC";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            boolean first = true;

            while (rs.next()) {
                String ht = rs.getString("hall_ticket");
                comboHallTickets.addItem(ht);

                if (first) {
                    comboHallTickets.setSelectedItem(ht);
                    loadStudentData(ht); // auto-load first paid hall ticket
                    first = false;
                }
            }

            if (first) { // No paid tickets found
                comboHallTickets.addItem("No paid hall tickets");
                JOptionPane.showMessageDialog(this, "No paid hall tickets found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading hall tickets from database.");
        }
    }

    private void loadStudentData(String hallTicketNo) {
        try {
            String sql = "SELECT s.first_name, s.last_name, s.prev_qualification, h.program_name " +
                         "FROM student_details s " +
                         "JOIN halltickets h ON s.id = h.stu_id " +
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
            String semSql = "SELECT DISTINCT semester FROM subjects WHERE program_name = ? ORDER BY semester ASC LIMIT 1";
            PreparedStatement pst2 = con.prepareStatement(semSql);
            pst2.setString(1, program);
            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                return rs2.getString("semester");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void loadExamTimetable(String program, String semester, String qualification) {
        DefaultTableModel model = (DefaultTableModel) tableSubjects.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT course_name, exam_date, start_time, end_time FROM exam_timetable " +
                         "WHERE program = ? AND semester = ? ORDER BY exam_date, start_time";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, program);
            pst.setString(2, semester);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String subject = rs.getString("course_name");
                String date = rs.getString("exam_date");
                String timings = rs.getString("start_time") + " - " + rs.getString("end_time");
                model.addRow(new Object[]{currentHallTicket, date, timings, subject});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createHallTicketImage(File file) {
        try {
            lblSelectTicket.setVisible(false);
            comboHallTickets.setVisible(false);
            btnGenerateHallTicket.setVisible(false);
            btnDownloadAll.setVisible(false);

            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            this.paint(g2);
            g2.dispose();
            ImageIO.write(image, "png", file);

            lblSelectTicket.setVisible(true);
            comboHallTickets.setVisible(true);
            btnGenerateHallTicket.setVisible(true);
            btnDownloadAll.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void downloadAllHallTickets() {
        try {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.setDialogTitle("Select folder to save all hall tickets");

            int option = folderChooser.showSaveDialog(this);
            if (option != JFileChooser.APPROVE_OPTION) return;

            File folder = folderChooser.getSelectedFile();

            String sql = "SELECT h.hall_ticket " +
                         "FROM halltickets h " +
                         "JOIN exam_fee e ON h.hall_ticket = e.hallticket_no " +
                         "WHERE UPPER(TRIM(e.status))='PAID'";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            ArrayList<String> hallTickets = new ArrayList<>();
            while (rs.next()) {
                hallTickets.add(rs.getString("hall_ticket"));
            }

            for (String ht : hallTickets) {
                comboHallTickets.setSelectedItem(ht);
                Thread.sleep(300); // small delay for GUI refresh
                File file = new File(folder, "HallTicket_" + ht + ".png");
                createHallTicketImage(file);
            }

            JOptionPane.showMessageDialog(this, "All hall tickets downloaded successfully to:\n" + folder.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error downloading all hall tickets.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == comboHallTickets) {
            String hallTicketNo = (String) comboHallTickets.getSelectedItem();
            if (hallTicketNo != null && !hallTicketNo.equals("Select Hall Ticket") && !hallTicketNo.equals("No paid hall tickets")) {
                loadStudentData(hallTicketNo);
            }
        } else if (e.getSource() == btnGenerateHallTicket) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File("HallTicket_" + currentHallTicket + ".png"));
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                createHallTicketImage(fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Hall Ticket saved: " + fileChooser.getSelectedFile().getAbsolutePath());
            }
        } else if (e.getSource() == btnDownloadAll) {
            downloadAllHallTickets();
        }
    }
}
