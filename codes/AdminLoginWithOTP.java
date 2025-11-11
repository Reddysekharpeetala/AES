import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;
public class AdminLoginWithOTP extends JFrame implements ActionListener {
    JTextField emailField;
    JPasswordField passwordField;
    JButton loginBtn;
    Connection con;

    // For OTP
    String generatedOTP;
    String adminEmail;

    AdminLoginWithOTP() {
        setTitle("Admin Login with OTP");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        add(new JLabel(""));
        add(loginBtn);

        connectDB();
        setVisible(true);
    }

    public void connectDB() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/a_e_s", "root", "reddy123"); 
            // Change db name & password
            System.out.println("DB Connected...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginBtn) {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            try {
                PreparedStatement pst = con.prepareStatement("SELECT * FROM admin_users WHERE username=? AND password=?");
                pst.setString(1, email);
                pst.setString(2, password);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    adminEmail = email;
                    generatedOTP = generateOTP(6);
                    sendEmail(adminEmail, generatedOTP);
                    JOptionPane.showMessageDialog(this, "OTP sent to your email!");

                    new OTPVerificationFrame(generatedOTP);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Email/Password!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // OTP Generator
    public String generateOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return otp.toString();
    }

    // Send Email
    public void sendEmail(String to, String otp) {
        final String from = "mmukthananda577@gmail.com"; 
        final String pass = "coqd ehlx awrg hhuw"; // Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

       Session session = Session.getInstance(props,
    new jakarta.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, pass);
        }
    });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Your OTP for AES Admin Login");
            message.setText("Your OTP is: " + otp);
            Transport.send(message);
            System.out.println("OTP Email Sent!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OTP Verification Frame
    class OTPVerificationFrame extends JFrame implements ActionListener {
        JTextField otpField;
        JButton verifyBtn;
        String otp;

        OTPVerificationFrame(String otp) {
            this.otp = otp;

            setTitle("OTP Verification");
            setSize(300, 150);
            setLayout(new GridLayout(2, 2, 10, 10));

            add(new JLabel("Enter OTP:"));
            otpField = new JTextField();
            add(otpField);

            verifyBtn = new JButton("Verify");
            verifyBtn.addActionListener(this);
            add(new JLabel(""));
            add(verifyBtn);

            setVisible(true);
        }

        public void actionPerformed(ActionEvent ae) {
            String enteredOTP = otpField.getText();
            if (enteredOTP.equals(otp)) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome Admin.");
                dispose();
                // Here you can open Admin Dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid OTP!");
            }
        }
    }

    public static void main(String[] args) {
        new AdminLoginWithOTP();
    }
}
