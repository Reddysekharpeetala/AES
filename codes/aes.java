import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class aes extends JFrame implements ActionListener {
    JMenuBar mb;
    JMenu adminMenu, facultyMenu, studentMenu, aboutMenu;
    JMenuItem adminItem, facultyItem, studentItem, contactItem;
    Image backgroundImage;

    aes() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();


        backgroundImage = new ImageIcon("aes.png").getImage();

       
        setTitle("SHRI GNANAMBICA DEGREE COLLEGE");
        setSize(d.width, d.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(new BorderLayout());         	setContentPane(bgPanel);

       
        mb = new JMenuBar();
        mb.setBackground(new Color(156, 77, 9));        mb.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        adminMenu = new JMenu("ADMIN");
        facultyMenu = new JMenu("FACULTY");
        studentMenu = new JMenu("STUDENT");
        aboutMenu = new JMenu("ABOUT US");

       
        Font menuFont = new Font("Arial Black", Font.BOLD, 20);
        adminMenu.setFont(menuFont);
        facultyMenu.setFont(menuFont);
        studentMenu.setFont(menuFont);
        aboutMenu.setFont(menuFont);

        adminMenu.setForeground(Color.WHITE);
        facultyMenu.setForeground(Color.WHITE);
        studentMenu.setForeground(Color.WHITE);
        aboutMenu.setForeground(Color.WHITE);

        
        adminItem = new JMenuItem("LOGIN");
        facultyItem = new JMenuItem("LOGIN");
        studentItem = new JMenuItem("LOGIN");
        contactItem = new JMenuItem("CONTACT US");

        
        Font itemFont = new Font("Arial", Font.PLAIN, 20);
        adminItem.setFont(itemFont);
        facultyItem.setFont(itemFont);
        studentItem.setFont(itemFont);
        contactItem.setFont(itemFont);

      
        adminMenu.add(adminItem);
        facultyMenu.add(facultyItem);
        studentMenu.add(studentItem);
        aboutMenu.add(contactItem);

        
        mb.add(adminMenu);
        mb.add(facultyMenu);
        mb.add(studentMenu);

     
        mb.add(Box.createHorizontalGlue());
        mb.add(aboutMenu);

        setJMenuBar(mb);

      
        adminItem.addActionListener(this);
        facultyItem.addActionListener(this);
        studentItem.addActionListener(this);
        contactItem.addActionListener(this);

        setVisible(true);
    }

    public static void main(String args[]) 
    {
        new aes();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == adminItem) 
	{
            new admin();
        } 
	else if (ae.getSource() == facultyItem) 
	{
            new faculty();
        } 
	else if (ae.getSource() == studentItem) 
	{
            new student();
        } 
	else if (ae.getSource() == contactItem) 
	{
            JOptionPane.showMessageDialog(this,
                "📌 Shri Gnanambica Degree College (Autonomous)\n" +
                "3-153-5, Near R.T.C Bus stand,\n" +
                "Madanapalle-517325,\n" +
                "Annamaiah (Dist), Andhra Pradesh - India\n\n" +
                "📞 Ph: 08571-223199, 222215\n\n" +
                "📧 Email: gnanambicadegreecollege@gmail.com",
                "Contact Us",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
