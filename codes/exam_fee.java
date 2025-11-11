import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class exam_fee extends JPanel implements ActionListener {

    private JComboBox<String> studentCombo;
    private JTextField hallticketField, programField, feeField;
    private JButton submitBtn, refreshBtn;

    private Connection con;
    private PreparedStatement pst;

    // Map fullName -> {hallticket, programName}
    private Map<String, String[]> studentMap = new HashMap<>();

    // Program fees
    private Map<String, String> programFees = new HashMap<>();
    private String defaultFee = "1450";

    public exam_fee() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // Initialize program fees
        programFees.put("BCA-G", "1200");
        programFees.put("BCA-AI", "1300");
        programFees.put("BCOM-CA", "1100");
        programFees.put("BSC", "1250");
        programFees.put("BBA", "1300");

        // Initialize components
        studentCombo = new JComboBox<>();
        hallticketField = new JTextField(20);
        programField = new JTextField(20);
        feeField = new JTextField(20);
        submitBtn = new JButton("Submit");
        refreshBtn = new JButton("Refresh");

        // Make hallticket, program and fee fields uneditable
        hallticketField.setEditable(false);
        programField.setEditable(false);
        feeField.setEditable(false);
        feeField.setBackground(Color.LIGHT_GRAY);
        feeField.setHorizontalAlignment(JTextField.CENTER);

        // Fonts and sizes
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(250, 35);
        Dimension buttonSize = new Dimension(120, 40);

        studentCombo.setFont(fieldFont);
        hallticketField.setFont(fieldFont);
        hallticketField.setPreferredSize(fieldSize);
        programField.setFont(fieldFont);
        programField.setPreferredSize(fieldSize);
        feeField.setFont(fieldFont);
        feeField.setPreferredSize(fieldSize);

        submitBtn.setFont(fieldFont);
        submitBtn.setPreferredSize(buttonSize);
        refreshBtn.setFont(fieldFont);
        refreshBtn.setPreferredSize(buttonSize);

        // Layout components
        int y = 0;
        addComponent(this, new JLabel("Student Name:"), 0, y, 1, 1, labelFont);
        addComponent(this, studentCombo, 1, y++, 2, 1, fieldFont);

        addComponent(this, new JLabel("Hall Ticket No:"), 0, y, 1, 1, labelFont);
        addComponent(this, hallticketField, 1, y++, 2, 1, fieldFont);

        addComponent(this, new JLabel("Program Name:"), 0, y, 1, 1, labelFont);
        addComponent(this, programField, 1, y++, 2, 1, fieldFont);

        addComponent(this, new JLabel("Fee Amount:"), 0, y, 1, 1, labelFont);
        addComponent(this, feeField, 1, y++, 2, 1, fieldFont);

        addComponent(this, submitBtn, 0, y, 1, 1, fieldFont);
        addComponent(this, refreshBtn, 1, y, 1, 1, fieldFont);

        // Connect to database
        connectDB();

        // Load students
        loadStudents();

        // Update hallticket, program and fee on student selection
        studentCombo.addActionListener(e -> updateDetails());

        // Button actions
        submitBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123"
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadStudents() {
        try {
            pst = con.prepareStatement(
                "SELECT h.hall_ticket, s.id, s.first_name, s.last_name, h.program_name " +
                "FROM halltickets h LEFT JOIN student_details s ON h.stu_id = s.id"
            );
            ResultSet rs = pst.executeQuery();
            studentCombo.removeAllItems();
            studentMap.clear();
            while (rs.next()) {
                String hallticket = rs.getString("hall_ticket");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String programName = rs.getString("program_name");

                if (firstName == null) firstName = "";
                if (lastName == null) lastName = "";

                String fullName = (firstName + " " + lastName).trim();
                if (fullName.isEmpty()) fullName = "Unknown Student";

                studentCombo.addItem(fullName);
                studentMap.put(fullName, new String[]{hallticket, programName});
            }

            // Update fee for first student by default
            if (studentCombo.getItemCount() > 0) {
                studentCombo.setSelectedIndex(0);
                updateDetails();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateDetails() {
        String fullName = (String) studentCombo.getSelectedItem();
        if (fullName == null) return;
        String[] details = studentMap.get(fullName);
        if (details != null) {
            String hallticket = details[0];
            String programName = details[1];

            hallticketField.setText(hallticket);
            programField.setText(programName);

            // Set fee automatically based on program
            String fee = programFees.getOrDefault(programName, defaultFee);
            feeField.setText(fee);
        } else {
            hallticketField.setText("");
            programField.setText("");
            feeField.setText(defaultFee);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            submitFee();
        } else if (e.getSource() == refreshBtn) {
            loadStudents();
        }
    }

    private void submitFee() {
        String fullName = (String) studentCombo.getSelectedItem();
        if (fullName == null || fullName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student.");
            return;
        }
        String[] details = studentMap.get(fullName);
        if (details == null) {
            JOptionPane.showMessageDialog(this, "Invalid student selected.");
            return;
        }
        String hallticket = details[0];
        String programName = details[1];
        String fee = feeField.getText().trim();

        try {
            // Check if student already paid
            pst = con.prepareStatement(
                "SELECT COUNT(*) FROM exam_fee WHERE hallticket_no=? AND program_name=? AND status='PAID'"
            );
            pst.setString(1, hallticket);
            pst.setString(2, programName);
            ResultSet rs = pst.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "This student has already paid the fee.");
                return;
            }

            // Insert new payment
            pst = con.prepareStatement(
                "INSERT INTO exam_fee(hallticket_no, program_name, fee_amount, status) VALUES (?, ?, ?, 'PAID')"
            );
            pst.setString(1, hallticket);
            pst.setString(2, programName);
            pst.setString(3, fee);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Fee submitted successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving fee: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Utility method to add components with GridBagLayout
    public void addComponent(Container container, Component comp, int x, int y, int w, int h, Font font) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        if (comp instanceof JLabel || comp instanceof JButton || comp instanceof JTextField || comp instanceof JComboBox) {
            comp.setFont(font);
        }
        container.add(comp, gc);
    }
}
