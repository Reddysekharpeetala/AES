import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class faculty extends JFrame implements ActionListener {
    JTextField t1;
    JPasswordField t2;
    JButton login;
    JPanel card;
    GridBagConstraints gc;
    JMenuItem backItem;
    JLabel title, l1, l2, l3;
    JComboBox<String> deptCombo;

    faculty() {
        setTitle("Faculty Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(200, 220, 250));

        // --- MENU BAR ---
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        backItem = new JMenuItem("<BACK");
        backItem.addActionListener(this);
        menu.add(backItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Card panel
        card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(800, 600));
        card.setBackground(new Color(245, 249, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        gc = new GridBagConstraints();
        gc.insets = new Insets(15, 15, 15, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        title = new JLabel("Faculty Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        addComponent(title, 0, 0, 1, 2);

        // Labels
        l1 = new JLabel("Enter Email:");
        l1.setFont(new Font("Arial", Font.PLAIN, 22));
        addComponent(l1, 0, 1, 1, 1);

        l2 = new JLabel("Enter Password:");
        l2.setFont(new Font("Arial", Font.PLAIN, 22));
        addComponent(l2, 0, 2, 1, 1);

        l3 = new JLabel("Select Department:");
        l3.setFont(new Font("Arial", Font.PLAIN, 22));
        addComponent(l3, 0, 3, 1, 1);

        // Text fields
        t1 = new JTextField();
        t1.setFont(new Font("Arial", Font.BOLD, 28));
        t1.setPreferredSize(new Dimension(400, 50));
        t1.setMargin(new Insets(5, 10, 5, 10));
        addComponent(t1, 1, 1, 1, 1);

        t2 = new JPasswordField();
        t2.setFont(new Font("Arial", Font.BOLD, 28));
        t2.setPreferredSize(new Dimension(400, 50));
        t2.setMargin(new Insets(5, 10, 5, 10));
        addComponent(t2, 1, 2, 1, 1);

        // Department Combo Box
        deptCombo = new JComboBox<>();
        deptCombo.setFont(new Font("Arial", Font.PLAIN, 22));
        deptCombo.addItem("Computer Science");
        deptCombo.addItem("Mathematics");
        deptCombo.addItem("Physics");
        deptCombo.addItem("Statistics");
        deptCombo.addItem("Chemistry");
        deptCombo.addItem("Electronics");
        deptCombo.setPreferredSize(new Dimension(400, 50));
        addComponent(deptCombo, 1, 3, 1, 1);

        // Login button
        login = new JButton("LOGIN");
        login.setFont(new Font("Arial", Font.BOLD, 22));
        login.setFocusPainted(false);
        login.setBackground(new Color(200, 220, 250));
        login.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2, true));
        addComponent(login, 0, 4, 2, 2);
        login.addActionListener(this);

        add(card);
        setVisible(true);
    }

    // Helper method
    public void addComponent(Component c, int x, int y, int height, int width) {
        gc.gridx = x;
        gc.gridy = y;
        gc.gridheight = height;
        gc.gridwidth = width;
        card.add(c, gc);
    }

    // Action events
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String username = t1.getText();
            String password = new String(t2.getPassword());
            String department = deptCombo.getSelectedItem().toString();

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");

                String sql = "SELECT * FROM faculty_details WHERE emp_email=? AND emp_ph=? AND emp_dept=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, department);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String facultyEmail = rs.getString("emp_email"); 
                    String otp = generateOTP();
                    sendOTP(facultyEmail, otp);

                    String enteredOTP = JOptionPane.showInputDialog(this,
                            "Enter the OTP sent to your email:");

                    if (otp.equals(enteredOTP)) {
                        JOptionPane.showMessageDialog(this, "Login Successful!");
                        new faculty_view();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid OTP! Login Failed.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username, Password, or Department!");
                }

                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (ae.getSource() == backItem) {
            new aes();
            dispose();
        }
    }

    // OTP Generator
    public String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send OTP with fallback (587 TLS -> 465 SSL)
    public void sendOTP(String recipientEmail, String otp) {
        String senderEmail = "reddysekharladdu@gmail.com";     
        String senderPassword = "kzzd pvro pdvp ocmq";   
        // First try with TLS (587)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        //session.setDebug(true); // debug logs

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Faculty Login OTP");
            message.setText("Dear Faculty,\n\nYour OTP is: " + otp + "\n\nDo not share it with anyone.");

            Transport.send(message); // try sending with 587
            JOptionPane.showMessageDialog(this, "OTP sent successfully to " + recipientEmail);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("⚠️ 587 failed, trying with SSL (465)...");

            // Fallback with SSL (465)
            try {
                Properties sslProps = new Properties();
                sslProps.put("mail.smtp.host", "smtp.gmail.com");
                sslProps.put("mail.smtp.port", "465");
                sslProps.put("mail.smtp.auth", "true");
                sslProps.put("mail.smtp.ssl.enable", "true");
                sslProps.put("mail.smtp.ssl.trust", "smtp.gmail.com");

                Session sslSession = Session.getInstance(sslProps, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });
                sslSession.setDebug(true);

                Message sslMessage = new MimeMessage(sslSession);
                sslMessage.setFrom(new InternetAddress(senderEmail));
                sslMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                sslMessage.setSubject("Faculty Login OTP");
                sslMessage.setText("Dear Faculty,\n\nYour OTP is: " + otp + "\n\nDo not share it with anyone.");

                Transport.send(sslMessage); // send with 465
                JOptionPane.showMessageDialog(this, "OTP sent successfully (SSL mode) to " + recipientEmail);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(this, "Failed to send OTP: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new faculty();
    }
}
