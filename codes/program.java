import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class program extends JFrame implements ActionListener 
{
    JMenuBar mb;
    JMenu adm, dat, course, contact;
    JMenuItem bcaItem, bscItem, bbaItem, bcomItem, contactItem; 
    JLabel l1, l2;
    GridBagConstraints gc;
    Image backgroundImage;

    program() 
    {
        setTitle("Automated Examination System");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(d.width, d.height);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);

        backgroundImage = new ImageIcon("sekhar.jpg").getImage();

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setPreferredSize(new Dimension(800, 600));

        // ==== MENU BAR ====
        mb = new JMenuBar();
        setJMenuBar(mb);

        adm = new JMenu("ADMIN");
        adm.setFont(new Font("Arial Black", Font.BOLD, 15));

        dat = new JMenu("DATA");
        dat.setFont(new Font("Arial Black", Font.BOLD, 15));

        course = new JMenu("Courses");
        course.setFont(new Font("Arial Black", Font.BOLD, 15));

        // Add course menu items
        bcaItem = new JMenuItem("BCA");
        bscItem = new JMenuItem("BSC");
        bbaItem = new JMenuItem("BBA");
        bcomItem = new JMenuItem("BCOM");

        course.add(bcaItem);
        course.add(bscItem);
        course.add(bbaItem);
        course.add(bcomItem);

        // Register action listeners
        bcaItem.addActionListener(this);
        bscItem.addActionListener(this);
        bbaItem.addActionListener(this);
        bcomItem.addActionListener(this);

        // ==== CONTACT MENU ====
        contact = new JMenu("Contacts");
        contact.setFont(new Font("Arial Black", Font.BOLD, 15));

        contactItem = new JMenuItem("Contact Us");
        contactItem.addActionListener(this);
        contact.add(contactItem);

        // Menu placement (Courses & Contacts right side)
        mb.add(adm);
        mb.add(dat);
        mb.add(Box.createHorizontalGlue());
        mb.add(course);
        mb.add(contact);

        // ==== HEADER LABEL ====
        l1 = new JLabel("Automated Examination System");
        l1.setFont(new Font("Algerian", Font.BOLD, 25));
        l1.setForeground(Color.BLUE);

        l2 = new JLabel("AES");
        l2.setFont(new Font("Arial Black", Font.BOLD, 15));
        l2.setForeground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(l1, BorderLayout.WEST);

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(headerPanel, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weighty = 100;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        mainPanel.add(l2, gc);

        setContentPane(mainPanel);
        setVisible(true);
    }

    // Handle all menu item actions here
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == bcaItem) {
            JOptionPane.showMessageDialog(this, "BCA Selected");
            saveCourseToDB("BCA", "Bca");
            new Bca();
        } 
        else if (src == bscItem) {
            JOptionPane.showMessageDialog(this, "BSC Selected");
            saveCourseToDB("BSC", "Bsc");
            new Bsc();
        } 
        else if (src == bbaItem) {
            JOptionPane.showMessageDialog(this, "BBA Selected");
            saveCourseToDB("BBA", "Bba");
            new Bba();
        } 
        else if (src == bcomItem) {
            JOptionPane.showMessageDialog(this, "BCOM Selected");
            saveCourseToDB("BCOM", "Bcom");
            new Bcom();
        }
        else if (src == contactItem) {
            JOptionPane.showMessageDialog(this, "Contact menu clicked!");
        }
    }

    // Save course to DB
    private void saveCourseToDB(String courseName, String tableName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");

            String query = "INSERT INTO " + tableName + " (course_name) VALUES(?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, courseName);
            pst.executeUpdate();

            con.close();
            System.out.println("Course saved in " + tableName + ": " + courseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new program();
    }
}
