import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class fac_add extends JPanel implements ActionListener {

    JTextField emp_name, dob, emp_email, emp_ph, emp_exp, emp_qua, emp_dept, address;
    JButton submit, clear;
    GridBagConstraints gc;

    Connection con;
    PreparedStatement pst;

    public fac_add() {
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(800, 600));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Faculty Form", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel emp_name1 = new JLabel("Employee Name:");
        JLabel dob1 = new JLabel("Employee DOB (dd-mm-yyyy):");
        JLabel emp_email1 = new JLabel("Employee Email:");
        JLabel emp_ph1 = new JLabel("Employee Phone No:");
        JLabel emp_dept1 = new JLabel("Employee Department:");
        JLabel emp_qua1 = new JLabel("Employee Qualification:");
        JLabel emp_exp1 = new JLabel("Employee Experience:");
        JLabel addressLabel = new JLabel("Address:");

        Font lblFont = new Font("Arial", Font.BOLD, 20);
        emp_name1.setFont(lblFont);
        dob1.setFont(lblFont);
        emp_email1.setFont(lblFont);
        emp_ph1.setFont(lblFont);
        emp_dept1.setFont(lblFont);
        emp_qua1.setFont(lblFont);
        emp_exp1.setFont(lblFont);
        addressLabel.setFont(lblFont);

        Dimension fieldSize = new Dimension(500, 40);
        Font textFont = new Font("Arial", Font.PLAIN, 20);

        emp_name = new JTextField();
        emp_name.setFont(textFont);
        emp_name.setPreferredSize(fieldSize);

        dob = new JTextField();
        dob.setFont(textFont);
        dob.setPreferredSize(fieldSize);
        addDocumentFilter(dob, "[0-9\\-]"); // Allow digits and hyphen, including empty for clearing

        emp_email = new JTextField();
        emp_email.setFont(textFont);
        emp_email.setPreferredSize(fieldSize);

        emp_ph = new JTextField();
        emp_ph.setFont(textFont);
        emp_ph.setPreferredSize(fieldSize);
        addDocumentFilter(emp_ph, "\\d"); // Allow digits only, including empty for clearing

        emp_dept = new JTextField();
        emp_dept.setFont(textFont);
        emp_dept.setPreferredSize(fieldSize);

        emp_qua = new JTextField();
        emp_qua.setFont(textFont);
        emp_qua.setPreferredSize(fieldSize);

        emp_exp = new JTextField();
        emp_exp.setFont(textFont);
        emp_exp.setPreferredSize(fieldSize);

        address = new JTextField();
        address.setFont(textFont);
        address.setPreferredSize(fieldSize);

        submit = new JButton("Submit");
        submit.setPreferredSize(new Dimension(150, 35));
        submit.setFocusPainted(false);

        clear = new JButton("Clear");
        clear.setPreferredSize(new Dimension(150, 35));

        addComponent(card, title, 0, 0, 2, 1);
        addComponent(card, emp_name1, 0, 1, 1, 1);
        addComponent(card, emp_name, 1, 1, 1, 1);
        addComponent(card, dob1, 0, 2, 1, 1);
        addComponent(card, dob, 1, 2, 1, 1);
        addComponent(card, emp_email1, 0, 3, 1, 1);
        addComponent(card, emp_email, 1, 3, 1, 1);
        addComponent(card, emp_ph1, 0, 4, 1, 1);
        addComponent(card, emp_ph, 1, 4, 1, 1);
        addComponent(card, emp_dept1, 0, 5, 1, 1);
        addComponent(card, emp_dept, 1, 5, 1, 1);
        addComponent(card, emp_qua1, 0, 6, 1, 1);
        addComponent(card, emp_qua, 1, 6, 1, 1);
        addComponent(card, emp_exp1, 0, 7, 1, 1);
        addComponent(card, emp_exp, 1, 7, 1, 1);
        addComponent(card, addressLabel, 0, 8, 1, 1);
        addComponent(card, address, 1, 8, 1, 1);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submit);
        buttonPanel.add(clear);
        addComponent(card, buttonPanel, 0, 9, 2, 1);

        add(card);

        submit.addActionListener(this);
        clear.addActionListener(this);

        connectDatabase();
    }

    private void addComponent(JPanel panel, Component c, int x, int y, int w, int h) {
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.weightx = 2.0;
        gc.weighty = 0.0;
        panel.add(c, gc);
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Update these credentials with your own
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
            System.out.println("Database Connected");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
        }
    }

    private void addDocumentFilter(JTextField textField, String regex) {
        AbstractDocument doc = (AbstractDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.isEmpty() || string.matches(regex + "+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.isEmpty() || text.matches(regex + "+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submit) {
            submitForm();
        } else if (e.getSource() == clear) {
            clearForm();
        }
    }

    private void clearForm() {
        SwingUtilities.invokeLater(() -> {
            emp_name.setText("");
            dob.setText("");
            emp_email.setText("");
            emp_ph.setText("");
            emp_dept.setText("");
            emp_qua.setText("");
            emp_exp.setText("");
            address.setText("");
            emp_name.requestFocus();
        });
    }

    private void submitForm() {
        try {
            String empName = emp_name.getText().trim();
            String empDob = dob.getText().trim();
            String empEmail = emp_email.getText().trim();
            String empPh = emp_ph.getText().trim();
            String empDept = emp_dept.getText().trim();
            String empQua = emp_qua.getText().trim();
            String empExp = emp_exp.getText().trim();
            String empAddr = address.getText().trim();

            if (empName.isEmpty() || empDob.isEmpty() || empEmail.isEmpty() || empPh.isEmpty()
                    || empDept.isEmpty() || empQua.isEmpty() || empExp.isEmpty() || empAddr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }

            if (!empPh.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits");
                return;
            }

            if (!empDob.matches("\\d{2}-\\d{2}-\\d{4}")) {
                JOptionPane.showMessageDialog(this, "DOB must be in dd-mm-yyyy format with 4-digit year");
                return;
            }

            if (!isValidDate(empDob)) {
                JOptionPane.showMessageDialog(this, "Enter a valid date for DOB");
                return;
            }

            if (!empEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email address");
                return;
            }

            // Convert DOB to yyyy-MM-dd
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(empDob);
            String formattedDob = mysqlFormat.format(date);

            String query = "INSERT INTO faculty_details (emp_name, dob, emp_email, emp_ph, emp_dept, emp_qua, emp_exp, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, empName);
            pst.setString(2, formattedDob);
            pst.setString(3, empEmail);
            pst.setString(4, empPh);
            pst.setString(5, empDept);
            pst.setString(6, empQua);
            pst.setString(7, empExp);
            pst.setString(8, empAddr);

            int result = pst.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Data Submitted Successfully!");
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit data.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private boolean isValidDate(String dob) {
        try {
            String[] parts = dob.split("-");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            if (month < 1 || month > 12) return false;
            if (day < 1) return false;

            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                daysInMonth[1] = 29;
            }
            return day <= daysInMonth[month - 1];
        } catch (Exception e) {
            return false;
        }
    }
}
