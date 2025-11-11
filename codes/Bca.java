import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class Bca extends JFrame implements ActionListener 
{
    JComboBox<String> semesterBox;
    JList<String> commonList, programList;
    JButton okButton, clearButton, backButton;
    GridBagConstraints gc;
    String program = "BCA";

    String[][] commonSubjects = {
        {"English", "Telugu"},
        {"English", "Telugu"},
        {"Communication Skills", "General Knowledge"},
        {"Soft Skills", "Ethics"},
        {"Research Methodology"},
        {"Project Presentation"}
    };

    String[][] bcaSubjects = {
        {"Java Programming", "Data Structures"},
        {"Database Management", "Web Development"},
        {"Python Programming", "Computer Networks"},
        {"Operating Systems", "Software Engineering"},
        {"AI Fundamentals", "Mobile App Development"},
        {"Final Year Project"}
    };

    public Bca() 
    {
        setTitle(program + " Course Details");
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width,d.height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
	getContentPane().setBackground(Color.pink);

        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("< BACK");
        backButton.addActionListener(this);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        Font labelFont = new Font("Arial", Font.BOLD, 14);

        addLabel(centerPanel, "Select Semester:", 0, 0, labelFont);
        String[] semesters = {"1", "2", "3", "4", "5", "6"};
        semesterBox = new JComboBox<>(semesters);
        semesterBox.addActionListener(this);
        addComponent(centerPanel, semesterBox, 1, 0);

        addLabel(centerPanel, "Common Subjects:", 0, 1, labelFont);
        commonList = new JList<>();
        JScrollPane commonScroll = new JScrollPane(commonList);
        addComponent(centerPanel, commonScroll, 1, 1);

        addLabel(centerPanel, program + " Subjects:", 0, 2, labelFont);
        programList = new JList<>();
        JScrollPane programScroll = new JScrollPane(programList);
        addComponent(centerPanel, programScroll, 1, 2);

        okButton = new JButton("OK");
        clearButton = new JButton("Clear");
        okButton.addActionListener(this);
        clearButton.addActionListener(this);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(okButton);
        bottomPanel.add(clearButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    void addLabel(JPanel panel, String text, int x, int y, Font font) 
    {
        JLabel label = new JLabel(text);
        label.setFont(font);
        gc.gridx = x;
        gc.gridy = y;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(label, gc);
    }

    void addComponent(JPanel panel, Component comp, int x, int y) 
    {
        gc.gridx = x;
        gc.gridy = y;
        gc.anchor = GridBagConstraints.WEST;
        panel.add(comp, gc);
    }

    void clearForm() 
    {
        semesterBox.setSelectedIndex(0);
        commonList.setListData(new String[]{});
        programList.setListData(new String[]{});
    }

    void updateSubjects() 
    {
        int semester = semesterBox.getSelectedIndex();
        if (semester >= 0) 
        {
            commonList.setListData(commonSubjects[semester]);
            programList.setListData(bcaSubjects[semester]);
        }
    }

    boolean storeInDatabase(String program, int semester, List<String> commonSubjects, List<String> programSubjects) 
    {
        String tableName = program.toLowerCase() + "_sem" + semester;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) 
        {
            String sql = "INSERT INTO " + tableName + " (group_type, subjects) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "Common");
            ps.setString(2, String.join(", ", commonSubjects));
            ps.executeUpdate();

            ps.setString(1, "Program-Oriented");
            ps.setString(2, String.join(", ", programSubjects));
            ps.executeUpdate();

            return true;
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        Object source = e.getSource();

        if (source == semesterBox) 
        {
            updateSubjects();
        } 
        else if (source == okButton) 
        {
            int semester = semesterBox.getSelectedIndex() + 1;
            List<String> selectedCommon = List.of(commonSubjects[semester - 1]);
            List<String> selectedProgram = List.of(bcaSubjects[semester - 1]);

            if (storeInDatabase(program, semester, selectedCommon, selectedProgram)) 
            {
                JOptionPane.showMessageDialog(this, "Data stored successfully for " + program + " Semester " + semester);
                dispose();
                new studentdetails();
            } 
            else 
            {
                JOptionPane.showMessageDialog(this, "Error storing data in database!");
            }
        } 
        else if (source == clearButton) 
        {
            clearForm();
        } 
        else if (source == backButton) 
        {
            dispose();
            new program(); 
        }
    }

    public static void main(String[] args) 
    {
        new Bca();
    }
}
