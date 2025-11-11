import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_timetable extends JPanel implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnFetch;

    faculty_timetable() {
        setLayout(new BorderLayout(10,10));

        // Columns: ID, Course, Day, Time
        String[] cols = {"ID","Course","Day","Time"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Fetch button
        btnFetch = new JButton("Fetch Timetable");
        add(btnFetch, BorderLayout.SOUTH);
        btnFetch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            // Load JDBC driver and connect
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan","root","reddy123");

            // Fetch timetable
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM timetable");

            model.setRowCount(0); // Clear table
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("course"),
                    rs.getString("day"),
                    rs.getString("time")
                });
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage());
        } finally {
            try{if(rs!=null) rs.close();}catch(Exception e1){}
            try{if(st!=null) st.close();}catch(Exception e1){}
            try{if(con!=null) con.close();}catch(Exception e1){}
        }
    }
}
