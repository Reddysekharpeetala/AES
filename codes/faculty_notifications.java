import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_notifications extends JPanel implements ActionListener {
    JTextField txtMessage;
    JButton btnSend, btnFetch;
    JTable table;
    DefaultTableModel model;

    faculty_notifications() {
        setLayout(new BorderLayout(10,10));

        // Top panel for input
        JPanel top = new JPanel();
        txtMessage = new JTextField(20);
        btnSend = new JButton("Send Notification");
        btnFetch = new JButton("Fetch Notifications");
        top.add(new JLabel("Message:"));
        top.add(txtMessage);
        top.add(btnSend);
        top.add(btnFetch);
        add(top, BorderLayout.NORTH);

        // Table for displaying notifications
        String[] cols = {"ID","Message","Created At"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Action listeners
        btnSend.addActionListener(this);
        btnFetch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection con = null;
        PreparedStatement ps = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan","root","reddy123");

            if(e.getSource() == btnSend) {
                ps = con.prepareStatement("INSERT INTO notifications(message, created_at) VALUES (?, NOW())");
                ps.setString(1, txtMessage.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Notification sent!");
            } else if(e.getSource() == btnFetch) {
                model.setRowCount(0);
                st = con.createStatement();
                rs = st.executeQuery("SELECT * FROM notifications ORDER BY created_at DESC");
                while(rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at")
                    });
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());
        } finally {
            try{if(rs!=null) rs.close();}catch(Exception e1){}
            try{if(ps!=null) ps.close();}catch(Exception e1){}
            try{if(st!=null) st.close();}catch(Exception e1){}
            try{if(con!=null) con.close();}catch(Exception e1){}
        }
    }
}
