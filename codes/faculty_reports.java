import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_reports extends JPanel implements ActionListener {
    JTextField txtStudentId;
    JButton btnGenerate;
    JTextArea reportArea;

    faculty_reports() {
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel();
        txtStudentId = new JTextField(10);
        btnGenerate = new JButton("Generate Report");
        top.add(new JLabel("Student ID:"));
        top.add(txtStudentId);
        top.add(btnGenerate);
        add(top, BorderLayout.NORTH);

        reportArea = new JTextArea();
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        btnGenerate.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan","root","reddy123");

            int sid = Integer.parseInt(txtStudentId.getText());
            StringBuilder sb = new StringBuilder();

            ps = con.prepareStatement(
                "SELECT COUNT(*) AS total, SUM(status='Present') AS present FROM attendance WHERE student_id=?");
            ps.setInt(1, sid);
            rs = ps.executeQuery();
            if(rs.next()) {
                sb.append("Attendance: ").append(rs.getInt("present"))
                  .append("/").append(rs.getInt("total")).append("\n");
            }
            rs.close(); ps.close();

            ps = con.prepareStatement("SELECT subject, marks FROM marks WHERE student_id=?");
            ps.setInt(1, sid);
            rs = ps.executeQuery();
            sb.append("Marks:\n");
            while(rs.next()) {
                sb.append(rs.getString("subject")).append(": ").append(rs.getInt("marks")).append("\n");
            }

            reportArea.setText(sb.toString());

        } catch(Exception ex) { ex.printStackTrace(); }
        finally {
            try{if(rs!=null) rs.close();}catch(Exception e1){}
            try{if(ps!=null) ps.close();}catch(Exception e1){}
            try{if(con!=null) con.close();}catch(Exception e1){}
        }
    }
}
