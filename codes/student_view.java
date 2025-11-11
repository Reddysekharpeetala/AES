import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class student_view extends JFrame implements ActionListener {

    private JPanel rightPanel;
    private JButton viewCoursesBtn, viewExamBtn, downloadBtn, viewResultBtn;
    private String loggedHallTicket, loggedProgram;

    public student_view(String hallTicket, String program) {
        this.loggedHallTicket = hallTicket;
        this.loggedProgram = program;

        setTitle("Student Dashboard");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Left panel with buttons ---
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(220, 0));
        leftPanel.setBackground(new Color(168, 230, 221));
        leftPanel.setLayout(new GridLayout(0, 1, 0, 10));

        // Create buttons
        viewCoursesBtn = new JButton("Subjects");
        viewExamBtn = new JButton("Exam Timetable");
        downloadBtn = new JButton("Download Hall Ticket");
        viewResultBtn = new JButton("Result");

        JButton[] buttons = {viewCoursesBtn, viewExamBtn, downloadBtn, viewResultBtn};
        for (JButton btn : buttons) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(52, 152, 219));
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(200, 60));
            btn.addActionListener(this);
            leftPanel.add(btn);
        }

        add(leftPanel, BorderLayout.WEST);

        // --- Right panel ---
        rightPanel = new JPanel(new BorderLayout());
        add(rightPanel, BorderLayout.CENTER);

        // Show welcome image initially
        setRightPanelImage("muktha.png");

        // --- Menu bar with center title and Logout ---
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new BorderLayout());
        menuBar.setBackground(new Color(0, 102, 204));

        // Center: College title with two lines
        JLabel titleLabel = new JLabel(
                "<html><div style='text-align: center;'>Shri Gnanambica Degree College<br>(AUTONOMOUS)</div></html>"
        );
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuBar.add(titleLabel, BorderLayout.CENTER);

        // Right side: Logout button
        JPanel rightMenuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightMenuPanel.setOpaque(false); // transparent background
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(204, 0, 0));
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);

        logoutBtn.addActionListener(e -> {
            dispose();
            new aes();
        });

        rightMenuPanel.add(logoutBtn);
        menuBar.add(rightMenuPanel, BorderLayout.EAST);

        setJMenuBar(menuBar);

        setVisible(true);
    }

    // --- Set image in right panel ---
    private void setRightPanelImage(String imagePath) {
        rightPanel.removeAll();
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width - 220,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH
        );
        icon = new ImageIcon(img);
        JLabel lblImage = new JLabel(icon);
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(lblImage, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // --- Show Subjects View ---
    private void showSubjectsView() {
        rightPanel.removeAll();
        view_subject panel = new view_subject();
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // --- Show Exam Timetable ---
    private void showExamTimetable() {
        rightPanel.removeAll();
        view_exam_timetable panel = new view_exam_timetable(loggedHallTicket, loggedProgram);
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // --- Show Hall Ticket ---
    private void showHallTicket() {
        rightPanel.removeAll();
        stu_HallTicketGenerator panel = new stu_HallTicketGenerator(loggedHallTicket);
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    // --- Show Result Panel ---
    private void showResultPanel() {
        rightPanel.removeAll();
        view_Results panel = new view_Results(loggedHallTicket);
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewCoursesBtn) {
            showSubjectsView();
        } else if (e.getSource() == viewExamBtn) {
            showExamTimetable();
        } else if (e.getSource() == downloadBtn) {
            showHallTicket();
        } else if (e.getSource() == viewResultBtn) {
            showResultPanel();
        }
    }

}
