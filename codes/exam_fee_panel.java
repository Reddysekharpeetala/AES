import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class exam_fee_panel extends JPanel implements ActionListener {

    JLabel lblProgram, lblSemester, lblStudentId, lblFee;
    JComboBox<String> cmbProgram, cmbSemester;
    JTextField txtStudentId, txtFee;
    JButton btnSubmit, btnClear;
    GridBagConstraints gc;

    Connection con;

    public exam_fee_panel() {
        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.insets = new Insets(15, 15, 15, 15);

        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        lblProgram = new JLabel("Program:");
        lblSemester = new JLabel("Semester:");
        lblStudentId = new JLabel("Student ID:");
        lblFee = new JLabel("Fee Amount:");

        lblProgram.setFont(labelFont);
        lblSemester.setFont(labelFont);
        lblStudentId.setFont(labelFont);
        lblFee.setFont(labelFont);

        // Connect to DB and load programs
        connectDB();
        cmbProgram = new JComboBox<>(loadPrograms());
        cmbProgram.setFont(fieldFont);

        String[] semesters = {"Sem1","Sem2","Sem3","Sem4","Sem5","Sem6"};
        cmbSemester = new JComboBox<>(semesters);
        cmbSemester.setFont(fieldFont);

        txtStudentId = new JTextField(20);
        txtFee = new JTextField(20);
        txtStudentId.setFont(fieldFont);
        txtFee.setFont(fieldFont);

        // Restrict txtFee to numbers only
        txtFee.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if(str == null) return;
                String currentText = getText(0, getLength());
                if ((str.matches("[0-9.]") && !currentText.contains(".")) || str.matches("[0-9]")) {
                    super.insertString(offs, str, a);
                }
            }
        });

        btnSubmit = new JButton("Submit");
        btnClear = new JButton("Clear");
        btnSubmit.setFont(labelFont);
        btnClear.setFont(labelFont);

        // Add components using helper method
        int row = 0;
        add(lblProgram, row, 0, 1, 1);
        add(cmbProgram, row++, 1, 1, 1);

        add(lblSemester, row, 0, 1, 1);
        add(cmbSemester, row++, 1, 1, 1);

        add(lblStudentId, row, 0, 1, 1);
        add(txtStudentId, row++, 1, 1, 1);

        add(lblFee, row, 0, 1, 1);
        add(txtFee, row++, 1, 1, 1);

        add(btnSubmit, row, 0, 1, 1);
        add(btnClear, row, 1, 1, 1);

        setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));

        btnSubmit.addActionListener(this);
        btnClear.addActionListener(this);
    }

    // Helper method for GridBagLayout
    public void add(Component cc, int r, int c, int w, int h){
        gc.gridx = c;
        gc.gridy = r;
        gc.gridwidth = w;
        gc.gridheight = h;
        gc.fill = GridBagConstraints.BOTH;
        super.add(cc,gc);
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan","root","reddy123");
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Database connection failed!");
        }
    }

    // Load program names dynamically from the programs table
    private String[] loadPrograms() {
        Vector<String> programs = new Vector<>();
        try {
            String sql = "SELECT program_name FROM programs";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                programs.add(rs.getString("program_name"));
            }
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to load programs from DB");
        }
        return programs.toArray(new String[0]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnSubmit){
            String program = cmbProgram.getSelectedItem().toString();
            String sem = cmbSemester.getSelectedItem().toString();
            String sid = txtStudentId.getText().trim();
            String fee = txtFee.getText().trim();

            if(program.isEmpty() || sem.isEmpty() || sid.isEmpty() || fee.isEmpty()){
                JOptionPane.showMessageDialog(this,"Fill all fields!");
                return;
            }

            try{
                String sql = "INSERT INTO exam_fee(student_id, program, semester, fee_amount) VALUES(?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, sid);
                pst.setString(2, program);
                pst.setString(3, sem);
                pst.setDouble(4, Double.parseDouble(fee));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this,"Fee saved successfully!");
            } catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,"Error saving fee!");
            }
        } else if(e.getSource() == btnClear){
            if(cmbProgram.getItemCount()>0) cmbProgram.setSelectedIndex(0);
            cmbSemester.setSelectedIndex(0);
            txtStudentId.setText("");
            txtFee.setText("");
        }
    }
}
