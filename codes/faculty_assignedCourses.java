import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_assignedCourses extends JPanel implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnFetch;

    faculty_assignedCourses() {
        setLayout(new BorderLayout(10, 10));

        // Match columns with programs table
        String[] cols = {"Program ID", "Program Name", "Duration"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);

        // Increase table size
        table.setRowHeight(30); // bigger rows
        table.setFont(new Font("Serif", Font.PLAIN, 16)); // bigger text
        table.getTableHeader().setFont(new Font("Serif", Font.BOLD, 16));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(600, 300)); // bigger scroll area
        add(scroll, BorderLayout.CENTER);

        btnFetch = new JButton("Refresh / Fetch Courses");
        btnFetch.setFont(new Font("Arial", Font.BOLD, 14));
        add(btnFetch, BorderLayout.SOUTH);

        btnFetch.addActionListener(this);

        // Load data when panel opens
        fetchData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFetch) {
            fetchData();
        }
    }

    private void fetchData() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM programs");

            model.setRowCount(0); // Clear old data

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("program_id"),
                        rs.getString("program_name"),
                        rs.getString("duration")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e1) {}
            try { if (st != null) st.close(); } catch (Exception e1) {}
            try { if (con != null) con.close(); } catch (Exception e1) {}
        }
    }
}
