import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.*;

public class stu_add extends JPanel implements ActionListener {

    JTextField first_name, last_name, father_name, mother_name, dob, stu_email, stu_ph, stu_aadhar, address;
    JComboBox<String> courseCombo, prevQuaCombo;
    JRadioButton m, f;
    JButton submit, clear;
    ButtonGroup g;
    GridBagConstraints gc;

    private Connection con;

    public stu_add() {
        setLayout(new GridBagLayout());
        connectDB();  

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(850, 650));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Student Form", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        addComponent(card, title, 0, 0, 2, 1);

        Font lblFont = new Font("Arial", Font.BOLD, 20);
        JLabel firstNameLabel = new JLabel("First Name:"); firstNameLabel.setFont(lblFont);
        JLabel lastNameLabel = new JLabel("Last Name:"); lastNameLabel.setFont(lblFont);
        JLabel fatherNameLabel = new JLabel("Father Name:"); fatherNameLabel.setFont(lblFont);
        JLabel motherNameLabel = new JLabel("Mother Name:"); motherNameLabel.setFont(lblFont);
        JLabel dobLabel = new JLabel("DOB (dd-mm-yyyy):"); dobLabel.setFont(lblFont);
        JLabel genderLabel = new JLabel("Gender:"); genderLabel.setFont(lblFont);
        JLabel emailLabel = new JLabel("Email:"); emailLabel.setFont(lblFont);
        JLabel phoneLabel = new JLabel("Phone No:"); phoneLabel.setFont(lblFont);
        JLabel aadharLabel = new JLabel("Aadhar No (xxxx-xxxx-xxxx):"); aadharLabel.setFont(lblFont);
        JLabel courseLabel = new JLabel("Course:"); courseLabel.setFont(lblFont);
        JLabel prevQuaLabel = new JLabel("Previous Qualification:"); prevQuaLabel.setFont(lblFont);
        JLabel addressLabel = new JLabel("Address:"); addressLabel.setFont(lblFont);

        Dimension fieldSize = new Dimension(400, 35);
        Font textFont = new Font("Arial", Font.BOLD, 20);

        first_name = new JTextField(); first_name.setFont(textFont); first_name.setPreferredSize(fieldSize);
        last_name = new JTextField(); last_name.setFont(textFont); last_name.setPreferredSize(fieldSize);
        father_name = new JTextField(); father_name.setFont(textFont); father_name.setPreferredSize(fieldSize);
        mother_name = new JTextField(); mother_name.setFont(textFont); mother_name.setPreferredSize(fieldSize);
        dob = new JTextField(); dob.setFont(textFont); dob.setPreferredSize(fieldSize);
        stu_email = new JTextField(); stu_email.setFont(textFont); stu_email.setPreferredSize(fieldSize);
        stu_ph = new JTextField(); stu_ph.setFont(textFont); stu_ph.setPreferredSize(fieldSize);
        stu_aadhar = new JTextField(); stu_aadhar.setFont(textFont); stu_aadhar.setPreferredSize(fieldSize);
        address = new JTextField(); address.setFont(textFont); address.setPreferredSize(fieldSize);

        // Courses
        courseCombo = new JComboBox<>();
        courseCombo.setFont(textFont); courseCombo.setPreferredSize(fieldSize);
        loadPrograms();

        // Previous Qualification ComboBox (MPC, BIPC, CEC, HEC)
        prevQuaCombo = new JComboBox<>();
        prevQuaCombo.setFont(textFont); prevQuaCombo.setPreferredSize(fieldSize);
        prevQuaCombo.addItem("Select Qualification");
        prevQuaCombo.addItem("MPC");
        prevQuaCombo.addItem("BIPC");
        prevQuaCombo.addItem("CEC");
        prevQuaCombo.addItem("HEC");

        // Gender
        m = new JRadioButton("Male"); f = new JRadioButton("Female");
        g = new ButtonGroup(); g.add(m); g.add(f);

        // DOB formatting
        dob.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = dob.getText();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) { e.consume(); return; }
                if (Character.isDigit(c)) {
                    if (text.length() == 2 || text.length() == 5) dob.setText(text + "-");
                    if (text.length() >= 10) e.consume();
                }
            }
        });

        // Aadhaar formatting
        stu_aadhar.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = stu_aadhar.getText().replaceAll("-", "");
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) { e.consume(); return; }
                if (Character.isDigit(c)) {
                    if (text.length() >= 12) { e.consume(); return; }
                    text += c;
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        formatted.append(text.charAt(i));
                        if ((i == 3 || i == 7) && i != text.length() - 1) formatted.append("-");
                    }
                    stu_aadhar.setText(formatted.toString()); e.consume();
                }
            }
        });

        // Phone numeric only
        stu_ph.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if(!Character.isDigit(e.getKeyChar())) e.consume(); }
        });

        // Add components
        int row = 1;
        addComponent(card, firstNameLabel, 0, row, 1, 1); addComponent(card, first_name, 1, row++, 1, 1);
        addComponent(card, lastNameLabel, 0, row, 1, 1); addComponent(card, last_name, 1, row++, 1, 1);
        addComponent(card, fatherNameLabel, 0, row, 1, 1); addComponent(card, father_name, 1, row++, 1, 1);
        addComponent(card, motherNameLabel, 0, row, 1, 1); addComponent(card, mother_name, 1, row++, 1, 1);
        addComponent(card, dobLabel, 0, row, 1, 1); addComponent(card, dob, 1, row++, 1, 1);
        addComponent(card, genderLabel, 0, row, 1, 1);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); genderPanel.add(m); genderPanel.add(f);
        addComponent(card, genderPanel, 1, row++, 1, 1);
        addComponent(card, emailLabel, 0, row, 1, 1); addComponent(card, stu_email, 1, row++, 1, 1);
        addComponent(card, phoneLabel, 0, row, 1, 1); addComponent(card, stu_ph, 1, row++, 1, 1);
        addComponent(card, aadharLabel, 0, row, 1, 1); addComponent(card, stu_aadhar, 1, row++, 1, 1);
        addComponent(card, courseLabel, 0, row, 1, 1); addComponent(card, courseCombo, 1, row++, 1, 1);
        addComponent(card, prevQuaLabel, 0, row, 1, 1); addComponent(card, prevQuaCombo, 1, row++, 1, 1);
        addComponent(card, addressLabel, 0, row, 1, 1); addComponent(card, address, 1, row++, 1, 1);

        JPanel buttonPanel = new JPanel();
        submit = new JButton("Submit"); clear = new JButton("Clear");
        submit.setPreferredSize(new Dimension(120, 35)); clear.setPreferredSize(new Dimension(120, 35));
        buttonPanel.add(submit); buttonPanel.add(clear);
        addComponent(card, buttonPanel, 0, row++, 2, 1);

        add(card);
        submit.addActionListener(this); clear.addActionListener(this);
    }

    private void addComponent(JPanel panel, Component c, int x, int y, int w, int h) {
        gc.gridx = x; 
	gc.gridy = y; 
	gc.gridwidth = w; 
	gc.gridheight = h;
        gc.weightx = 1.0; 
	gc.weighty = 0.0; gc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(c, gc);
    }

    private void connectDB() {
        try { con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123"); }
        catch (SQLException e) { 
	e.printStackTrace(); 
	JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage()); }
    }

    private void loadPrograms() {
        try {
            String sql = "SELECT program_name FROM programs";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            courseCombo.removeAllItems(); courseCombo.addItem("Select Course");
            while(rs.next()) courseCombo.addItem(rs.getString("program_name"));
            rs.close(); pst.close();
        } catch(SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error loading programs: " + e.getMessage()); }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submit) submitForm(); else if(e.getSource() == clear) clearForm();
    }

    private void clearForm() {
        first_name.setText(""); 
	last_name.setText(""); 
	father_name.setText(""); 
	mother_name.setText("");
        dob.setText(""); 
	stu_email.setText(""); 
	stu_ph.setText(""); 
	stu_aadhar.setText(""); 
        courseCombo.setSelectedIndex(0); 
	address.setText(""); g.clearSelection();
        prevQuaCombo.setSelectedIndex(0);
    }

    private void submitForm() {
        try {
            String fname = first_name.getText().trim();
            String lname = last_name.getText().trim();
            String father = father_name.getText().trim();
            String mother = mother_name.getText().trim();
            String dobVal = dob.getText().trim();
            String gender = m.isSelected() ? "Male" : (f.isSelected() ? "Female" : "");
            String email = stu_email.getText().trim();
            String phone = stu_ph.getText().trim();
            String aadhar = stu_aadhar.getText().trim().replaceAll("-", "");
            String course = (String) courseCombo.getSelectedItem();
            String previousQua = (String) prevQuaCombo.getSelectedItem();
            String addr = address.getText().trim();

            if(fname.isEmpty() || lname.isEmpty() || father.isEmpty() || mother.isEmpty() ||
               dobVal.isEmpty() || gender.isEmpty() || email.isEmpty() || phone.isEmpty() ||
               aadhar.isEmpty() || course.equals("Select Course") || previousQua.equals("Select Qualification") ||
               addr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            if(!dobVal.matches("\\d{2}-\\d{2}-\\d{4}")) { JOptionPane.showMessageDialog(this, "DOB must be in dd-mm-yyyy format."); return; }
            if(phone.length() != 10) { JOptionPane.showMessageDialog(this, "Phone must be 10 digits."); return; }
            if(aadhar.length() != 12) { JOptionPane.showMessageDialog(this, "Aadhaar must be 12 digits."); return; }
            if(!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) { JOptionPane.showMessageDialog(this, "Invalid email format."); return; }

            // **Check for duplicate Aadhaar**
            String checkSql = "SELECT COUNT(*) FROM student_details WHERE aadhar = ?";
            PreparedStatement checkPst = con.prepareStatement(checkSql);
            checkPst.setString(1, aadhar);
            ResultSet rs = checkPst.executeQuery();
            if(rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "This Aadhaar number is already registered.");
                rs.close();
                checkPst.close();
                return;
            }
            rs.close();
            checkPst.close();

            // Insert new student
            String sql = "INSERT INTO student_details (first_name,last_name,father_name,mother_name,dob,gender,email,phone,aadhar,course,prev_qualification,address) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fname); pst.setString(2, lname); pst.setString(3, father); pst.setString(4, mother);
            String[] dobParts = dobVal.split("-"); pst.setString(5, dobParts[2]+"-"+dobParts[1]+"-"+dobParts[0]);
            pst.setString(6, gender); pst.setString(7, email); pst.setString(8, phone); pst.setString(9, aadhar);
            pst.setString(10, course); pst.setString(11, previousQua); pst.setString(12, addr);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            clearForm(); 
	   pst.close();

        } catch(Exception ex) { 
	ex.printStackTrace(); 
	JOptionPane.showMessageDialog(this, "Error: " + 	ex.getMessage()); }
    }
}
