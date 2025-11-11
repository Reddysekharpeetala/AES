import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class faculty_view extends JFrame implements ActionListener {

    // LEFT PANEL BUTTONS
    JButton btn_students, btn_courses, btn_subjects,
            btn_hallticket, btn_timetable, btn_marks;
    
    // LOGOUT BUTTON
    JButton btn_logout;

    JLabel l;
    JPanel contentPanel;
    JPanel studentsPanel, coursesPanel, subjectsPanel,
           hallticketPanel, timetablePanel, marksPanel;

    public faculty_view() {

        // ---------------- MENU BAR ----------------
        JMenuBar mb = new JMenuBar();
        mb.setBackground(new Color(70, 130, 180));
        mb.setLayout(new BorderLayout());
        setJMenuBar(mb);

        // College Name Label
        l = new JLabel("Shri Gnanambica Degree College");
        l.setFont(new Font("Arial", Font.BOLD, 26));
        l.setForeground(Color.WHITE);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        center.setBackground(new Color(70, 130, 180));
        center.add(l);
        mb.add(center, BorderLayout.CENTER);

        // Logout button on top-right of menubar
        btn_logout = new JButton("LOGOUT");
        btn_logout.setFont(new Font("Arial", Font.BOLD, 14));
        btn_logout.setBackground(new Color(220, 20, 60));
        btn_logout.setForeground(Color.WHITE);
        btn_logout.setFocusPainted(false);
        btn_logout.addActionListener(this);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(70, 130, 180));
        logoutPanel.add(btn_logout);

        mb.add(logoutPanel, BorderLayout.EAST);

        // ---------------- FRAME SETTINGS ----------------
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---------------- LEFT PANEL ----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        leftPanel.setBackground(new Color(142, 237, 232));
        leftPanel.setPreferredSize(new Dimension(180, d.height));

        // Initialize buttons
        btn_students   = new JButton("STUDENTS");
        btn_courses    = new JButton("COURSES");
        btn_subjects   = new JButton("SUBJECTS");
        btn_hallticket = new JButton("HALLTICKETS");
        btn_timetable  = new JButton("EXAM TIMETABLE");
        btn_marks      = new JButton("MARKS");

        JButton[] menuButtons = {btn_students, btn_courses, btn_subjects,
                                 btn_hallticket, btn_timetable, btn_marks};

        Dimension buttonSize = new Dimension(160, 40);
        for (JButton b : menuButtons) {
            b.setMaximumSize(buttonSize);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setFont(new Font("Arial", Font.BOLD, 16));
            b.setBackground(new Color(30, 144, 255));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 15))); // GAP
            leftPanel.add(b);
            b.addActionListener(this); // ActionListener added
        }

        add(leftPanel, BorderLayout.WEST);

        // ---------------- CENTER PANEL ----------------
        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);

        
        JPanel defaultPanel = new JPanel(new BorderLayout());
	ImageIcon icon = new ImageIcon("mm.jpg");        


	Image img = icon.getImage().getScaledInstance(1200, 700, 	Image.SCALE_SMOOTH);

	ImageIcon scaledIcon = new ImageIcon(img);


	JLabel label = new JLabel(scaledIcon);
	label.setHorizontalAlignment(JLabel.CENTER);
	label.setVerticalAlignment(JLabel.CENTER);

	defaultPanel.add(label, BorderLayout.CENTER);
	contentPanel.add(defaultPanel, "DEFAULT");

        // Other panels (replace with your actual panel classes)
        studentsPanel   = new stu_view();
        coursesPanel    = new pro_view();
        subjectsPanel   = new view_subject();
        hallticketPanel = new hallticket_view();
        timetablePanel  = new exam_timetable();
        marksPanel      = new Marks();

        contentPanel.add(studentsPanel, "STUDENTS");
        contentPanel.add(coursesPanel, "COURSES");
        contentPanel.add(subjectsPanel, "SUBJECTS");
        contentPanel.add(hallticketPanel, "HALLTICKET");
        contentPanel.add(timetablePanel, "TIMETABLE");
        contentPanel.add(marksPanel, "MARKS");

        // Show default panel initially
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "DEFAULT");

        setVisible(true);
    }

    // ---------------- ACTION LISTENER ----------------
    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        if (e.getSource() == btn_students) cl.show(contentPanel, "STUDENTS");
        else if (e.getSource() == btn_courses) cl.show(contentPanel, "COURSES");
        else if (e.getSource() == btn_subjects) cl.show(contentPanel, "SUBJECTS");
        else if (e.getSource() == btn_hallticket) cl.show(contentPanel, "HALLTICKET");
        else if (e.getSource() == btn_timetable) cl.show(contentPanel, "TIMETABLE");
        else if (e.getSource() == btn_marks) cl.show(contentPanel, "MARKS");
        else if (e.getSource() == btn_logout) {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose();
            new aes(); 
        }
    }

    public static void main(String args[]) {
        new faculty_view();
    }
}
