import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class add_subject extends JPanel implements ActionListener {

    JTextField name;
    JComboBox<String> categoryCombo, semesterCombo, programCombo;
    JButton submit, clear, addMinorBtn;
    GridBagConstraints gc;

    Connection con;
    PreparedStatement pst;

    public add_subject() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // ---------------- CARD PANEL ----------------
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(850, 400));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Add Subject", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        addComponent(card, title, 0, 0, 2, 1);

        Font lblFont = new Font("Arial", Font.BOLD, 20);
        Font textFont = new Font("Arial", Font.BOLD, 20);
        Dimension fieldSize = new Dimension(400, 40);

        // Subject Name
        addLabelAndField(card, "Subject Name:", lblFont, textFont, fieldSize, name = new JTextField(), 0, 1);

        // Program Combo
        programCombo = new JComboBox<>();
        programCombo.setFont(textFont);
        programCombo.setPreferredSize(fieldSize);
        addLabelAndField(card, "Program:", lblFont, textFont, fieldSize, programCombo, 0, 2);

        // Category Combo
        categoryCombo = new JComboBox<>(new String[]{"Languages", "Multidisciplinary", "Major", "Skills"});
        categoryCombo.setFont(textFont);
        categoryCombo.setPreferredSize(fieldSize);
        addLabelAndField(card, "Category:", lblFont, textFont, fieldSize, categoryCombo, 0, 3);

        // Semester Combo
        semesterCombo = new JComboBox<>();
        semesterCombo.setFont(textFont);
        semesterCombo.setPreferredSize(fieldSize);
        for (int i = 1; i <= 6; i++) semesterCombo.addItem("Semester " + i);
        addLabelAndField(card, "Semester:", lblFont, textFont, fieldSize, semesterCombo, 0, 4);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        submit = new JButton("Submit");
        submit.setFont(textFont);
        submit.setPreferredSize(new Dimension(150, 45));

        clear = new JButton("Clear");
        clear.setFont(textFont);
        clear.setPreferredSize(new Dimension(150, 45));

        addMinorBtn = new JButton("Add Minor");
        addMinorBtn.setFont(textFont);
        addMinorBtn.setPreferredSize(new Dimension(150, 45));

        buttonPanel.add(submit);
        buttonPanel.add(clear);
        buttonPanel.add(addMinorBtn);

        addComponent(card, buttonPanel, 0, 5, 2, 1);

        add(card);

        // Action listeners
        submit.addActionListener(this);
        clear.addActionListener(this);
        addMinorBtn.addActionListener(this);

        // Connect DB and load programs
        connectDB();
        loadPrograms();
    }

    private void addLabelAndField(JPanel panel, String labelText, Font lblFont, Font textFont, Dimension fieldSize, JComponent field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(lblFont);
        field.setFont(textFont);
        field.setPreferredSize(fieldSize);

        gc.gridx = x; gc.gridy = y; gc.gridwidth = 1;
        panel.add(label, gc);

        gc.gridx = x + 1; gc.gridy = y; gc.gridwidth = 1;
        panel.add(field, gc);
    }

    private void addComponent(JPanel panel, Component comp, int x, int y, int w, int h) {
        gc.gridx = x; gc.gridy = y;
        gc.gridwidth = w; gc.gridheight = h;
        gc.weightx = 1.0; gc.weighty = 0.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gc);
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan",
                    "root",
                    "reddy123"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
            submit.setEnabled(false);
            addMinorBtn.setEnabled(false);
        }
    }

    private void loadPrograms() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT program_name FROM programs");
            programCombo.removeAllItems();
            while(rs.next()){
                programCombo.addItem(rs.getString("program_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load programs: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submit) submitSubject();
        else if(e.getSource() == clear) clearFields();
        else if(e.getSource() == addMinorBtn) openAddMinorFrame();
    }

    private void submitSubject() {
        try {
            String subName = name.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();
            String semester = (String) semesterCombo.getSelectedItem();
            String program = (String) programCombo.getSelectedItem();

            if(subName.isEmpty() || category == null || semester == null || program == null){
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            String sql = "INSERT INTO subjects (subject_name, category, semester, program_name) VALUES (?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, subName);
            pst.setString(2, category);
            pst.setString(3, semester);
            pst.setString(4, program);

            int inserted = pst.executeUpdate();
            if(inserted > 0){
                JOptionPane.showMessageDialog(this, "Subject Saved Successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Save Subject.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        name.setText("");
        categoryCombo.setSelectedIndex(0);
        semesterCombo.setSelectedIndex(0);
        if(programCombo.getItemCount() > 0) programCombo.setSelectedIndex(0);
    }

    private void openAddMinorFrame() {
        JFrame minorFrame = new JFrame("Add Minor Subject");
        minorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        minorFrame.setSize(900, 550);
        minorFrame.setLocationRelativeTo(null);

        // Minor Panel
        JPanel minorPanel = new JPanel(new GridBagLayout());
        minorPanel.setBackground(Color.WHITE);
        GridBagConstraints mgc = new GridBagConstraints();
        mgc.insets = new Insets(10,10,10,10);
        mgc.fill = GridBagConstraints.HORIZONTAL;

        Font lblFont = new Font("Arial", Font.BOLD, 20);
        Font textFont = new Font("Arial", Font.BOLD, 20);
        Dimension fieldSize = new Dimension(400, 40);

        // Subject Name
        JTextField minorName = new JTextField();
        JLabel lblName = new JLabel("Subject Name:");
        lblName.setFont(lblFont);
        minorName.setFont(textFont);
        minorName.setPreferredSize(fieldSize);
        mgc.gridx=0; mgc.gridy=0; minorPanel.add(lblName, mgc);
        mgc.gridx=1; minorPanel.add(minorName, mgc);

        // Program Combo
        JComboBox<String> minorProgram = new JComboBox<>();
        JLabel lblProgram = new JLabel("Program:");
        lblProgram.setFont(lblFont);
        minorProgram.setFont(textFont);
        minorProgram.setPreferredSize(fieldSize);
        mgc.gridx=0; mgc.gridy=1; minorPanel.add(lblProgram, mgc);
        mgc.gridx=1; minorPanel.add(minorProgram, mgc);

        // Semester Combo (Semester 2 to 5)
        JComboBox<String> minorSemester = new JComboBox<>();
        for (int i=2;i<=5;i++) minorSemester.addItem("Semester " + i);
        JLabel lblSemester = new JLabel("Semester:");
        lblSemester.setFont(lblFont);
        minorSemester.setFont(textFont);
        minorSemester.setPreferredSize(fieldSize);
        mgc.gridx=0; mgc.gridy=2; minorPanel.add(lblSemester, mgc);
        mgc.gridx=1; minorPanel.add(minorSemester, mgc);

        // Category Combo
        JComboBox<String> minorCategory = new JComboBox<>(new String[]{"Finance", "Physics", "Mathematics", "Electronics", "Statistics"});
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(lblFont);
        minorCategory.setFont(textFont);
        minorCategory.setPreferredSize(fieldSize);
        mgc.gridx=0; mgc.gridy=3; minorPanel.add(lblCategory, mgc);
        mgc.gridx=1; minorPanel.add(minorCategory, mgc);

        // Buttons
        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(textFont);
        btnSubmit.setPreferredSize(new Dimension(150,45));
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(textFont);
        btnClear.setPreferredSize(new Dimension(150,45));

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnSubmit);
        btnPanel.add(btnClear);

        mgc.gridx=0; mgc.gridy=4; mgc.gridwidth=2;
        minorPanel.add(btnPanel, mgc);

        // Load programs
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT program_name FROM programs");
            minorProgram.removeAllItems();
            while(rs.next()){
                minorProgram.addItem(rs.getString("program_name"));
            }
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"Failed to load programs: "+ex.getMessage());
        }

        // Submit Action
        btnSubmit.addActionListener(ae -> {
            String subName = minorName.getText().trim();
            String semester = (String) minorSemester.getSelectedItem();
            String program = (String) minorProgram.getSelectedItem();
            String category = (String) minorCategory.getSelectedItem();

            if(subName.isEmpty() || semester==null || program==null || category==null){
                JOptionPane.showMessageDialog(minorFrame,"Please fill all fields!");
                return;
            }
            try{
                PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO minor_subjects (subject_name, program_name, semester, category) VALUES (?,?,?,?)"
                );
                pst.setString(1, subName);
                pst.setString(2, program);
                pst.setString(3, semester);
                pst.setString(4, category);
                int inserted = pst.executeUpdate();
                if(inserted>0){
                    JOptionPane.showMessageDialog(minorFrame,"Minor Subject Saved Successfully!");
                    minorName.setText("");
                    minorSemester.setSelectedIndex(0);
                    minorCategory.setSelectedIndex(0);
                    if(minorProgram.getItemCount()>0) minorProgram.setSelectedIndex(0);
                }
            } catch(SQLException ex){
                JOptionPane.showMessageDialog(minorFrame,"Error: "+ex.getMessage());
            }
        });

        btnClear.addActionListener(ae -> {
            minorName.setText("");
            minorSemester.setSelectedIndex(0);
            minorCategory.setSelectedIndex(0);
            if(minorProgram.getItemCount()>0) minorProgram.setSelectedIndex(0);
        });

        minorFrame.add(minorPanel);
        minorFrame.setVisible(true);
    }

    }
