import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class faculty_profile extends JPanel implements ActionListener {
    JTextField txtName, txtEmail, txtDept;
    JButton btnUpdate, btnFetch;
    int facultyId = 1; // Replace with logged-in faculty ID

    faculty_profile() {
        setLayout(new GridLayout(4,2,5,5));

        txtName = new JTextField(); txtEmail = new JTextField(); txtDept = new JTextField();
        btnUpdate = new JButton("Update"); btnFetch = new JButton("Load");

        add(new JLabel("Name:")); add(txtName);
        add(new JLabel("Email:")); add(txtEmail);
        add(new JLabel("Department:")); add(txtDept);
        add(btnUpdate); add(btnFetch);

        btnUpdate.addActionListener(this);
        btnFetch.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan","root","reddy123");

            if(e.getSource() == btnUpdate) {
                ps = con.prepareStatement(
                    "UPDATE faculty SET name=?, email=?, department=? WHERE id=?");
                ps.setString(1, txtName.getText());
                ps.setString(2, txtEmail.getText());
                ps.setString(3, txtDept.getText());
                ps.setInt(4, facultyId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this,"Profile updated!");
            }
            else if(e.getSource() == btnFetch) {
                ps = con.prepareStatement("SELECT * FROM faculty WHERE id=?");
                ps.setInt(1, facultyId);
                rs = ps.executeQuery();
                if(rs.next()) {
                    txtName.setText(rs.getString("name"));
                    txtEmail.setText(rs.getString("email"));
                    txtDept.setText(rs.getString("department"));
                }
            }
        } catch(Exception ex) { ex.printStackTrace(); }
        finally {
            try{if(rs!=null) rs.close();}catch(Exception e1){}
            try{if(ps!=null) ps.close();}catch(Exception e1){}
            try{if(con!=null) con.close();}catch(Exception e1){}
        }
    }
}
