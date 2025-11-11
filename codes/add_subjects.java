import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class add_subject extends JPanel implements ActionListener {
    JLabel lblProgram, lblSem, lblCategory, lblName;
    JComboBox<String> cmbProgram, cmbSem, cmbCategory;
    JTextField txtName;
    JButton btnAdd, btnClear;

    add_subject() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        lblProgram = new JLabel("Program ID:");
        lblSem = new JLabel("Semester:");
        lblCategory = new JLabel("Category:");
        lblName = new JLabel("Subject Name:");

        cmbProgram = new JComboBox<>();
        cmbSem = new JComboBox<>(new String[]{"1","2","3","4","5","6"});
        cmbCategory = new JComboBox<>(new String[]{"Major","Minor","Multidisciplinary","Skill","Language"});
        txtName = new JTextField(15);

        btnAdd = new JButton("Add Subject");
        btnClear = new JButton("Clear");

        loadPrograms();

        gc.gridx = 0; gc.gridy = 0; add(lblProgram, gc);
        gc.gridx = 1; add(cmbProgram, gc);
        gc.gridx = 0; gc.gridy = 1; add(lblSem, gc);
        gc.gridx = 1; add(cmbSem, gc);
        gc.gridx = 0; gc.gridy = 2; add(lblCategory, gc);
        gc.gridx = 1; add(cmbCategory, gc);
        gc.gridx = 0; gc.gridy = 3; add(lblName, gc);
        gc.gridx = 1; add(txtName, gc);
        gc.gridx = 0; gc.gridy = 4; add(btnAdd, gc);
        gc.gridx = 1; add(btnClear, gc);

        btnAdd.addActionListener(this);
        btnClear.addActionListener(this);
    }

    void loadPrograms() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/aes", "root", "password");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT program_id FROM programs");
            while (rs.next()) {
                cmbProgram.addItem(rs.getString("program_id"));
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            String program = (String) cmbProgram.getSelectedItem();
            int sem = Integer.parseInt((String)cmbSem.getSelectedItem());
            String category = (String) cmbCategory.getSelectedItem();
            String name = txtName.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter subject name!");
                return;
            }

            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO subjects(subject_name, category, semester, program_id) VALUES(?,?,?,?)"
                );
                ps.setString(1, name);
                ps.setString(2, category);
                ps.setInt(3, sem);
                ps.setString(4, program);
                ps.executeUpdate();
                con.close();
                JOptionPane.showMessageDialog(this, "Subject added successfully!");
                txtName.setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == btnClear) {
            txtName.setText("");
        }
    }
}
