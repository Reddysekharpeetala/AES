import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class student extends JFrame implements ActionListener {

    JLabel lblTitle, lblHallTicket, lblCourse;
    JTextField txtHallTicket;
    JComboBox<String> comboGroup;
    JButton btnLogin;
    private Connection con;
    private JPanel card;
    private GridBagConstraints gc;
    private JMenuBar mb;
    private JMenu menu;
    private JMenuItem backItem;

    public student() {
        setTitle("Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(200, 220, 250));

        connectDatabase();

        mb = new JMenuBar();
        setJMenuBar(mb);
        menu = new JMenu("Options");
        backItem = new JMenuItem("<BACK");
        backItem.addActionListener(this);
        menu.add(backItem);
        mb.add(menu);

        card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(700, 450));
        card.setBackground(new Color(245, 249, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)));

        gc = new GridBagConstraints();
        gc.insets = new Insets(15, 15, 15, 15);

        lblTitle = new JLabel("Student Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        addComponent(lblTitle, 0, 0, 1, 2);

        lblHallTicket = new JLabel("Hallticket No:");
        lblHallTicket.setFont(new Font("Arial", Font.PLAIN, 22));
        addComponent(lblHallTicket, 0, 1, 1, 1);

        txtHallTicket = new JTextField();
        txtHallTicket.setFont(new Font("Arial", Font.BOLD, 28));
        txtHallTicket.setPreferredSize(new Dimension(400, 50));
        txtHallTicket.setMargin(new Insets(5, 10, 5, 10));
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        addComponent(txtHallTicket, 1, 1, 1, 1);

        lblCourse = new JLabel("Group:");
        lblCourse.setFont(new Font("Arial", Font.PLAIN, 22));
        addComponent(lblCourse, 0, 2, 1, 1);

        comboGroup = new JComboBox<>();
        comboGroup.setFont(new Font("Arial", Font.BOLD, 28));
        comboGroup.setPreferredSize(new Dimension(400, 50));
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        addComponent(comboGroup, 1, 2, 1, 1);
        loadPrograms();

        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 22));
        btnLogin.setFocusPainted(false);
        btnLogin.setBackground(new Color(200, 220, 250));
        btnLogin.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2, true));
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        addComponent(btnLogin, 0, 3, 2, 2);
        btnLogin.addActionListener(this);

        add(card);
        setVisible(true);
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPrograms() {
        try {
            String sql = "SELECT program_name FROM programs";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            comboGroup.removeAllItems();
            while (rs.next()) {
                comboGroup.addItem(rs.getString("program_name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading programs: " + e.getMessage());
        }
    }

    private void addComponent(Component c, int x, int y, int height, int width) {
        gc.gridx = x;
        gc.gridy = y;
        gc.gridheight = height;
        gc.gridwidth = width;
        card.add(c, gc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String hallTicket = txtHallTicket.getText().trim();
            String selectedGroup = (String) comboGroup.getSelectedItem();
            if (hallTicket.isEmpty() || selectedGroup == null) {
                JOptionPane.showMessageDialog(this, "Please enter Hall Ticket and select Group");
                return;
            }
            try {
                String sql = "SELECT * FROM halltickets WHERE hall_ticket = ? AND program_name = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, hallTicket);
                pst.setString(2, selectedGroup);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    // ✅ Successful login, open student view
                    dispose();
                    new student_view(hallTicket, selectedGroup);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Hall Ticket or Group");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == backItem) {
            new aes(); // go back to main AES screen
            dispose();
        }
    }

    public static void main(String[] args) {
        new student();
    }
}
