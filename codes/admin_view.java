import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class admin_view extends JFrame implements ActionListener {

    JButton fac, fac_view, stu, stu_view, btn_addProgram, btn_course;
    JButton btn_addSubject, btn_viewSubject, btn_reports;
    JButton btn_examFee, btn_examTimetable, btn_generateHallPhoto;
    JButton btn_attendance, btn_marks, btn_resultGen, btn_logout;

    JPanel contentPanel;

    // Panels
    fac_add facPanel;
    fac_view facViewPanel;
    stu_add student;
    stu_view studentView;
    add_new_pro addprogram;
    pro_view viewProgram;
    add_subject addSubPanel;
    view_subject viewSubPanel;
    hallticket_view hallTicketPanel;
    exam_fee examFeePanel;
    exam_timetable examTimetablePanel;
    HallTicketGenerator hallPhotoPanel;
    ViewAttendancePanel attendancePanel;
    Marks marksPanel;
    ResultPanel resultGenPanel;
    JPanel welcomePanel;

    public admin_view() {
        // MENU BAR
        JMenuBar mb = new JMenuBar();
        JLabel l = new JLabel("Shri Gnanambica Degree College");
        l.setFont(new Font("Arial", Font.BOLD, 30));
        mb.setLayout(new BorderLayout());

        JPanel centerTitle = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerTitle.setBackground(new Color(0, 102, 204)); // Blue background
        l.setForeground(Color.WHITE);
        centerTitle.add(l);
        mb.add(centerTitle, BorderLayout.CENTER);

        // Logout button at top-right
        btn_logout = new JButton("Logout");
        btn_logout.addActionListener(this);
        btn_logout.setBackground(new Color(70, 130, 180));
        btn_logout.setForeground(Color.WHITE);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(0, 102, 204));
        rightPanel.add(btn_logout);
        mb.add(rightPanel, BorderLayout.EAST);

        setJMenuBar(mb);

        // FRAME SETUP
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // RIGHT PANEL (content)
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(new Color(121,121,121)); // Right panel background
        add(contentPanel, BorderLayout.CENTER);

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(201, 242, 234));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        Dimension buttonSize = new Dimension(200, 40);

        Color buttonBg = new Color(70, 130, 180); // Steel blue
        Color buttonFg = Color.BLACK;

        // FACULTY SECTION
        JPanel facultyPanel = new JPanel();
        facultyPanel.setBackground(Color.WHITE);
        facultyPanel.setLayout(new BoxLayout(facultyPanel, BoxLayout.Y_AXIS));
        facultyPanel.setBorder(BorderFactory.createTitledBorder("Faculty"));
        fac = new JButton("Faculty_Add");
        fac_view = new JButton("Faculty_View");
        fac.setMaximumSize(buttonSize);
        fac_view.setMaximumSize(buttonSize);
        facultyPanel.add(fac);
        facultyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        facultyPanel.add(fac_view);

        // STUDENT SECTION
        JPanel studentPanel = new JPanel();
        studentPanel.setBackground(Color.WHITE);
        studentPanel.setLayout(new BoxLayout(studentPanel, BoxLayout.Y_AXIS));
        studentPanel.setBorder(BorderFactory.createTitledBorder("Student"));
        stu = new JButton("Student_Add");
        stu_view = new JButton("Student_View");
        stu.setMaximumSize(buttonSize);
        stu_view.setMaximumSize(buttonSize);
        studentPanel.add(stu);
        studentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        studentPanel.add(stu_view);

        // PROGRAM & SUBJECT SECTION
        JPanel programPanel = new JPanel();
        programPanel.setBackground(Color.WHITE);
        programPanel.setLayout(new BoxLayout(programPanel, BoxLayout.Y_AXIS));
        programPanel.setBorder(BorderFactory.createTitledBorder("Program & Subject"));
        btn_addProgram = new JButton("New_Program");
        btn_course = new JButton("Courses_View");
        btn_addSubject = new JButton("Subject_Add");
        btn_viewSubject = new JButton("Subject_View");
        btn_reports = new JButton("HallTicket No");
        JButton[] progButtons = {btn_addProgram, btn_course, btn_addSubject, btn_viewSubject, btn_reports};
        for (JButton b : progButtons) {
            b.setMaximumSize(buttonSize);
            programPanel.add(b);
            programPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // EXAMS SECTION
        JPanel examsPanel = new JPanel();
        examsPanel.setBackground(Color.WHITE);
        examsPanel.setLayout(new BoxLayout(examsPanel, BoxLayout.Y_AXIS));
        examsPanel.setBorder(BorderFactory.createTitledBorder("Exams"));
        btn_examFee = new JButton("Exam Fee");
        btn_examTimetable = new JButton("Exam TimeTable");
        btn_generateHallPhoto = new JButton("Download Hall Photo");
        btn_attendance = new JButton("Attendance");
        btn_marks = new JButton("Marks");
        btn_resultGen = new JButton("Result Generation");
        JButton[] examButtons = {btn_examFee, btn_examTimetable, btn_generateHallPhoto, btn_attendance, btn_marks, btn_resultGen};
        for (JButton b : examButtons) {
            b.setMaximumSize(buttonSize);
            examsPanel.add(b);
            examsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // ADD SECTIONS TO LEFT PANEL
        leftPanel.add(facultyPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(studentPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(programPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(examsPanel);
        leftPanel.add(Box.createVerticalGlue());
        add(leftPanel, BorderLayout.WEST);

        // SET BUTTON COLORS
        JButton[] allButtons = {fac, fac_view, stu, stu_view, btn_addProgram, btn_course,
                btn_addSubject, btn_viewSubject, btn_reports, btn_examFee,
                btn_examTimetable, btn_generateHallPhoto, btn_attendance,
                btn_marks, btn_resultGen, btn_logout};
        for (JButton b : allButtons) {
            b.setBackground(buttonBg);
            b.setForeground(buttonFg);
            b.addActionListener(this);
        }

        // INIT CONTENT PANELS
        facPanel = new fac_add();
        facViewPanel = new fac_view();
        student = new stu_add();
        studentView = new stu_view();
        addprogram = new add_new_pro();
        viewProgram = new pro_view();
        addSubPanel = new add_subject();
        viewSubPanel = new view_subject();
        hallTicketPanel = new hallticket_view();
        examFeePanel = new exam_fee();
        examTimetablePanel = new exam_timetable();
        hallPhotoPanel = new HallTicketGenerator(); // Initialized here
        attendancePanel = new ViewAttendancePanel();
        marksPanel = new Marks();
        resultGenPanel = new ResultPanel();

        // WELCOME IMAGE PANEL
        welcomePanel = new JPanel(new BorderLayout());
        ImageIcon icon = new ImageIcon("muktha.png"); // Your image file path
        Image img = icon.getImage().getScaledInstance(1200, 700, Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel(new ImageIcon(img));
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(lblImage, BorderLayout.CENTER);

        // ADD PANELS TO CardLayout
        contentPanel.add(welcomePanel, "WELCOME");
        contentPanel.add(facPanel, "FACULTY_FORM");
        contentPanel.add(facViewPanel, "FACULTY_VIEW");
        contentPanel.add(student, "STUDENT_FORM");
        contentPanel.add(studentView, "STUDENT_VIEW");
        contentPanel.add(addprogram, "ADD_NEW_PRO");
        contentPanel.add(viewProgram, "VIEW_PRO");
        contentPanel.add(addSubPanel, "ADD_SUBJECT");
        contentPanel.add(viewSubPanel, "VIEW_SUBJECT");
        contentPanel.add(hallTicketPanel, "HALLTICKET_VIEW");
        contentPanel.add(examFeePanel, "EXAM_FEE");
        contentPanel.add(examTimetablePanel, "EXAM_TIMETABLE");
        contentPanel.add(attendancePanel, "ATTENDANCE");
        contentPanel.add(marksPanel, "MARKS");
        contentPanel.add(resultGenPanel, "RESULTPANEL");
        contentPanel.add(hallPhotoPanel, "HALL_PHOTO"); // Added upfront

        // SHOW WELCOME PANEL BY DEFAULT
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "WELCOME");

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        if (e.getSource() == fac) cl.show(contentPanel, "FACULTY_FORM");
        else if (e.getSource() == fac_view) { facViewPanel.loadData(); cl.show(contentPanel, "FACULTY_VIEW"); }
        else if (e.getSource() == stu) cl.show(contentPanel, "STUDENT_FORM");
        else if (e.getSource() == stu_view) { studentView.loadData("All"); cl.show(contentPanel, "STUDENT_VIEW"); }
        else if (e.getSource() == btn_addProgram) cl.show(contentPanel, "ADD_NEW_PRO");
        else if (e.getSource() == btn_course) cl.show(contentPanel, "VIEW_PRO");
        else if (e.getSource() == btn_addSubject) cl.show(contentPanel, "ADD_SUBJECT");
        else if (e.getSource() == btn_viewSubject) { viewSubPanel.loadData(); cl.show(contentPanel, "VIEW_SUBJECT"); }
        else if (e.getSource() == btn_reports) { hallTicketPanel.loadData(); cl.show(contentPanel, "HALLTICKET_VIEW"); }
        else if (e.getSource() == btn_examFee) cl.show(contentPanel, "EXAM_FEE");
        else if (e.getSource() == btn_examTimetable) cl.show(contentPanel, "EXAM_TIMETABLE");
        else if (e.getSource() == btn_generateHallPhoto) cl.show(contentPanel, "HALL_PHOTO"); // Just show
        else if (e.getSource() == btn_attendance) cl.show(contentPanel, "ATTENDANCE");
        else if (e.getSource() == btn_marks) cl.show(contentPanel, "MARKS");
        else if (e.getSource() == btn_resultGen) cl.show(contentPanel, "RESULTPANEL");
        else if (e.getSource() == btn_logout) 
	{
	dispose();
	new aes();
	}
    }

    public static void main(String[] args) {
        new admin_view();
    }
}
