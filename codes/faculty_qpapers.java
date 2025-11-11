import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_qpapers extends JPanel implements ActionListener {
    JTable table;
    DefaultTableModel model;
    JButton btnUpload, btnFetch;
    JTextField txtCourse, txtFilePath;

    faculty_qpapers() {
        setLayout(new BorderLayout(10,10));

        String[] cols = {"ID","Course","File Path"};
        model = new DefaultTableModel(cols,0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2,3,5,5));
        txtCourse = new JTextField();
        txtFilePath = new JTextField();
        btnUpload = new JButton("Upload QPaper");
        btnFetch = new JButton("Fetch QPapers");

        form.add(new JLabel("Course:")); form.add(txtCourse); form.add(btnUpload);
        form.add(new JLabel("File Path:")); form.add(txtFilePath); form.add(btnFetch);

        add(form, BorderLayout.NORTH);

        btnUpload.addActionListener(this);
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

            if(e.getSource() == btnUpload) {
                ps = con.prepareStatement("INSERT INTO qpapers(course,file_path) VALUES (?,?)");
                ps.setString(1, txtCourse.getText());
                ps.setString(2, txtFilePath.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"QPaper uploaded!");
            }
            else if(e.getSource() == btnFetch) {
                model.setRowCount(0);
                st = con.createStatement();
                rs = st.executeQuery("SELECT * FROM qpapers");
                while(rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("course"),
                        rs.getString("file_path")
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
