import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class studentdetails extends JFrame implements ActionListener {

    JTextField nameField, fatherNameField, motherNameField, dobField, emailField, phoneField, aadhaarField, aparIdField;
    JTextArea addressArea;
    JRadioButton maleRadio, femaleRadio;
    JButton submitButton, clearButton;
    ButtonGroup genderGroup;
    GridBagConstraints gbc;

    public studentdetails() {
        setTitle("Student Registration Form");
        Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width,d.height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        getContentPane().setBackground(Color.blue);

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        addLabel("Student Name:", 0, 0, labelFont);
        nameField = new JTextField(20);
        addComponent(nameField, 1, 0);

        addLabel("Father's Name:", 0, 1, labelFont);
        fatherNameField = new JTextField(20);
        addComponent(fatherNameField, 1, 1);

        addLabel("Mother's Name:", 0, 2, labelFont);
        motherNameField = new JTextField(20);
        addComponent(motherNameField, 1, 2);

        addLabel("Date of Birth (dd-mm-yyyy):", 0, 3, labelFont);
        dobField = new JTextField(20);
        addComponent(dobField, 1, 3);

        addLabel("Gender:", 0, 4, labelFont);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        addComponent(genderPanel, 1, 4);

        addLabel("Aadhaar Number:", 0, 5, labelFont);
        aadhaarField = new JTextField(20);
        addComponent(aadhaarField, 1, 5);

        addLabel("Apar ID:", 0, 6, labelFont);
        aparIdField = new JTextField(20);
        addComponent(aparIdField, 1, 6);

        addLabel("Email:", 0, 7, labelFont);
        emailField = new JTextField(20);
        addComponent(emailField, 1, 7);

        addLabel("Phone Number:", 0, 8, labelFont);
        phoneField = new JTextField(20);
        addComponent(phoneField, 1, 8);

        addLabel("Address:", 0, 9, labelFont);
        addressArea = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(addressArea);
        addComponent(scroll, 1, 9);

        submitButton = new JButton("Submit");
        clearButton = new JButton("Clear");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        gbc.gridwidth = 2;
        addComponent(buttonPanel, 0, 10);
        gbc.gridwidth = 1;

        submitButton.addActionListener(this);
        clearButton.addActionListener(this);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    void addLabel(String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        add(label, gbc);
    }

    void addComponent(Component comp, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        add(comp, gbc);
    }

    void clearForm() {
        nameField.setText("");
        fatherNameField.setText("");
        motherNameField.setText("");
        dobField.setText("");
        genderGroup.clearSelection();
        aadhaarField.setText("");
        aparIdField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
    }

    void submitForm() {
        String name = nameField.getText().trim();
        String fatherName = fatherNameField.getText().trim();
        String motherName = motherNameField.getText().trim();
        String dob = dobField.getText().trim(); // dd-mm-yyyy
        String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "");
        String aadhaar = aadhaarField.getText().trim();
        String aparId = aparIdField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressArea.getText().trim();

        // Validation
        if (name.isEmpty() || fatherName.isEmpty() || motherName.isEmpty() || dob.isEmpty() || gender.isEmpty()
                || aadhaar.isEmpty() || aparId.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields.");
            return;
        }

        if (!aadhaar.matches("\\d{12}")) {
            JOptionPane.showMessageDialog(this, "Aadhaar must be 12 digits.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits.");
            return;
        }

        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        // Convert DOB to yyyy-mm-dd
        String[] dobParts = dob.split("-");
        if (dobParts.length != 3) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use dd-mm-yyyy.");
            return;
        }
        String dobFormatted = dobParts[2] + "-" + dobParts[1] + "-" + dobParts[0];

        // Insert into database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");

            String sql = "INSERT INTO student_details " +
                    "(name, father_name, mother_name, dob, gender, aadhaar, apar_id, email, phone, address) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, fatherName);
            ps.setString(3, motherName);
            ps.setString(4, dobFormatted);
            ps.setString(5, gender);
            ps.setString(6, aadhaar);
            ps.setString(7, aparId);
            ps.setString(8, email);
            ps.setString(9, phone);
            ps.setString(10, address);

            ps.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(this, "Student data submitted successfully!");
            clearForm();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error storing data: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            submitForm();
        } else if (e.getSource() == clearButton) {
            clearForm();
        }
    }

    public static void main(String[] args) {
        new studentdetails();
    }
}
