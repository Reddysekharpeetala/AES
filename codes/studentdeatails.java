import javax.swing.*;
import java.awt.*;

public class studentdetails extends JFrame {
    JTextField nameField, fatherNameField, motherNameField, dobField, emailField, phoneField, aadhaarField, aparIdField;
    JTextArea addressArea;
    JComboBox<String> courseBox;
    JRadioButton maleRadio, femaleRadio;
    JButton submitButton, clearButton;
    ButtonGroup genderGroup;
    GridBagConstraints gbc;

    studentdetails() {
        setTitle("Student Registration Form");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        // Student Name
        addLabel("Student Name:", 0, 0, labelFont);
        nameField = new JTextField(20);
        addComponent(nameField, 1, 0);

        // Father's Name
        addLabel("Father's Name:", 0, 1, labelFont);
        fatherNameField = new JTextField(20);
        addComponent(fatherNameField, 1, 1);

        // Mother's Name
        addLabel("Mother's Name:", 0, 2, labelFont);
        motherNameField = new JTextField(20);
        addComponent(motherNameField, 1, 2);

        // Date of Birth
        addLabel("Date of Birth (dd-mm-yyyy):", 0, 3, labelFont);
        dobField = new JTextField(20);
        addComponent(dobField, 1, 3);

        // Gender
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

        // Aadhaar Number
        addLabel("Aadhaar Number:", 0, 5, labelFont);
        aadhaarField = new JTextField(20);
        addComponent(aadhaarField, 1, 5);

        // Apar ID
        addLabel("Apar ID:", 0, 6, labelFont);
        aparIdField = new JTextField(20);
        addComponent(aparIdField, 1, 6);

        // Course
        addLabel("Course:", 0, 7, labelFont);
        String[] courses = {"BCA", "BBA", "B.Sc", "B.Com", "BA", "MCA", "MBA"};
        courseBox = new JComboBox<>(courses);
        addComponent(courseBox, 1, 7);

        // Email
        addLabel("Email:", 0, 8, labelFont);
        emailField = new JTextField(20);
        addComponent(emailField, 1, 8);

        // Phone
        addLabel("Phone Number:", 0, 9, labelFont);
        phoneField = new JTextField(20);
        addComponent(phoneField, 1, 9);

        // Address
        addLabel("Address:", 0, 10, labelFont);
        addressArea = new JTextArea(3, 20);
        JScrollPane scroll = new JScrollPane(addressArea);
        addComponent(scroll, 1, 10);

        // Buttons
        submitButton = new JButton("Submit");
        clearButton = new JButton("Clear");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        gbc.gridwidth = 2;
        addComponent(buttonPanel, 0, 11);
        gbc.gridwidth = 1;

        // Action Listeners
        submitButton.addActionListener(e -> submitForm());
        clearButton.addActionListener(e -> clearForm());

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

    void submitForm() {
        String name = nameField.getText();
        String fatherName = fatherNameField.getText();
        String motherName = motherNameField.getText();
        String dob = dobField.getText();
        String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "Not Selected");
        String aadhaar = aadhaarField.getText();
        String aparId = aparIdField.getText();
        String course = (String) courseBox.getSelectedItem();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressArea.getText();

        JOptionPane.showMessageDialog(this,
                "Registration Successful!\n\n" +
                        "Name: " + name +
                        "\nFather's Name: " + fatherName +
                        "\nMother's Name: " + motherName +
                        "\nDOB: " + dob +
                        "\nGender: " + gender +
                        "\nAadhaar: " + aadhaar +
                        "\nApar ID: " + aparId +
                        "\nCourse: " + course +
                        "\nEmail: " + email +
                        "\nPhone: " + phone +
                        "\nAddress: " + address
        );
    }

    void clearForm() {
        nameField.setText("");
        fatherNameField.setText("");
        motherNameField.setText("");
        dobField.setText("");
        genderGroup.clearSelection();
        aadhaarField.setText("");
        aparIdField.setText("");
        courseBox.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
    }

    public static void main(String[] args) {
        new studentdetails();
    }
}
