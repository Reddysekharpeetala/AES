import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_marks extends JPanel implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnInsert, btnFetch;
    JTextField txtStuId, txtCourseId, txtMarks;

    faculty_marks() {
        setLayout(new BorderLayout(10,10));

        String[] cols = {"Student ID", "Course ID", "Marks"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);

        JPanel form = new JPanel(new GridLayout(4,2,5,5));
        txtStuId = new JTextField();
        txtCourseId = new JTextField();
        txtMarks = new JTextField();
        btnInsert = new JButton("Insert Marks");
        btnFetch = new JButton("Fetch Marks");

        form.add(new JLabel("Student ID:")); form.add(txtStuId);
        form.add(new JLabel("Course ID:")); form.add(txtCourseId);
        form.add(new JLabel("Marks:")); form.add(txtMarks);
        form.add(btnInsert); form.add(btnFetch);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnInsert.addActionListener(this);
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

            if(e.getSource() == btnInsert) {
                ps = con.prepareStatement(
                    "INSERT INTO marks(student_id,course_id,marks) VALUES (?,?,?)");
                ps.setInt(1, Integer.parseInt(txtStuId.getText()));
                ps.setInt(2, Integer.parseInt(txtCourseId.getText()));
                ps.setInt(3, Integer.parseInt(txtMarks.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Marks inserted!");
            }
            else if(e.getSource() == btnFetch) {
                model.setRowCount(0);
                st = con.createStatement();
                rs = st.executeQuery("SELECT * FROM marks");
                while(rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getInt("course_id"),
                        rs.getInt("marks")
                    });
                }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        finally {
            try{if(rs!=null) rs.close();}catch(Exception e1){}
            try{if(ps!=null) ps.close();}catch(Exception e1){}
            try{if(st!=null) st.close();}catch(Exception e1){}
            try{if(con!=null) con.close();}catch(Exception e1){}
        }
    }
}
