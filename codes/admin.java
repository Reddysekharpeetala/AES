import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

class admin extends JFrame implements ActionListener {
    JTextField t1;
    JPasswordField t2;
    JButton login;
    JPanel card;
    GridBagConstraints gc;
    JMenuItem backMenu;

    String otpGenerated;

    admin() {
        setTitle("Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(200, 220, 250));

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        backMenu = new JMenuItem("BACK");
        backMenu.addActionListener(this);
        menu.add(backMenu);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(700, 450));
        card.setBackground(new Color(245, 249, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        gc = new GridBagConstraints();
        gc.insets = new Insets(15, 15, 15, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(0, 51, 102));
        addComponent(title, 0, 0, 1, 2);

        JLabel l1 = new JLabel("Email");
        l1.setFont(new Font("Arial", Font.PLAIN, 22));
        l1.setForeground(new Color(50, 50, 50));
        addComponent(l1, 0, 1, 1, 1);

        t1 = new JTextField();
        t1.setFont(new Font("Arial", Font.PLAIN, 22));
        t1.setPreferredSize(new Dimension(400, 50));
        t1.setMargin(new Insets(5, 10, 5, 10));
        addComponent(t1, 1, 1, 1, 1);

        JLabel l2 = new JLabel("Password");
        l2.setFont(new Font("Arial", Font.PLAIN, 22));
        l2.setForeground(new Color(50, 50, 50));
        addComponent(l2, 0, 2, 1, 1);

        t2 = new JPasswordField();
        t2.setFont(new Font("Arial", Font.PLAIN, 22));
        t2.setPreferredSize(new Dimension(400, 50));
        t2.setMargin(new Insets(5, 10, 5, 10));
        addComponent(t2, 1, 2, 1, 1);

        login = new JButton("LOGIN");
        login.setFont(new Font("Arial", Font.BOLD, 22));
        login.setFocusPainted(false);
        login.setBackground(new Color(200, 220, 250));
        login.setForeground(new Color(0, 51, 102));
        login.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2, true));
        addComponent(login, 0, 3, 2, 2);
        login.addActionListener(this);
        add(card);
        setVisible(true);
    }

    public void addComponent(Component c, int x, int y, int height, int width) {
        gc.gridx = x;
        gc.gridy = y;
        gc.gridheight = height;
        gc.gridwidth = width;
        card.add(c, gc);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            String username = t1.getText();
            String password = new String(t2.getPassword());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/A_E_S", "root", "reddy123");

                String sql = "SELECT * FROM admin_users WHERE username=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String email = username;                     		    otpGenerated = generateOTP();
                    sendOTPEmail(email, otpGenerated);

                    String enteredOTP = JOptionPane.showInputDialog(this, "Please Enter OTP sent to your email:");
                    if (enteredOTP != null && enteredOTP.equals(otpGenerated)) {
                        JOptionPane.showMessageDialog(this, "Login Successful!");
                        new admin_view();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid OTP!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
                }

                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else if (ae.getSource() == backMenu) {
            dispose();
            new aes();
        }
    }

    public String generateOTP() {
        int otp = 100000 + new Random().nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String toEmail, String otp) {
        String fromEmail = "reddysekharladdu@gmail.com";
        String password = "kzzd pvro pdvp ocmq"; 
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("\nWelcome to AES System,Your Login OTP is:");
            message.setText("Your OTP is: " + otp);
            Transport.send(message);
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Failed to send OTP: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new admin();
    }
}
