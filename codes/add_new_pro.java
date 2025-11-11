import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class add_new_pro extends JPanel implements ActionListener {

    JTextField id, name, type, dura, dept;
    JButton submit, clear;
    GridBagConstraints gc;

    Connection con;
    PreparedStatement pst;

    public add_new_pro() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // ---------------- CARD PANEL ----------------
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(850, 500));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Program Form", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        addComponent(card, title, 0, 0, 2, 1);

        Font lblFont = new Font("Arial", Font.BOLD, 20);
        Font textFont = new Font("Arial", Font.BOLD, 20);
        Dimension fieldSize = new Dimension(400, 40);

        // Labels and Fields directly on card panel
        addLabelAndField(card, "Program ID:", lblFont, textFont, fieldSize, id = new JTextField(), 0, 1);
        addLabelAndField(card, "Program Name:", lblFont, textFont, fieldSize, name = new JTextField(), 0, 2);
        addLabelAndField(card, "Program Type:", lblFont, textFont, fieldSize, type = new JTextField(), 0, 3);
        addLabelAndField(card, "Duration:", lblFont, textFont, fieldSize, dura = new JTextField(), 0, 4);
        addLabelAndField(card, "Department:", lblFont, textFont, fieldSize, dept = new JTextField(), 0, 5);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        submit = new JButton("Submit"); submit.setFont(textFont); submit.setPreferredSize(new Dimension(150, 45));
        clear = new JButton("Clear"); clear.setFont(textFont); clear.setPreferredSize(new Dimension(150, 45));
        buttonPanel.add(submit); buttonPanel.add(clear);
        addComponent(card, buttonPanel, 0, 6, 2, 1);

        add(card);

        submit.addActionListener(this);
        clear.addActionListener(this);

        connectDB();
    }

    private void addLabelAndField(JPanel panel, String labelText, Font lblFont, Font textFont, Dimension fieldSize, JTextField field, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(lblFont);
        field.setFont(textFont);
        field.setPreferredSize(fieldSize);

        gc.gridx = x; gc.gridy = y; gc.gridwidth = 1;
        panel.add(label, gc);

        gc.gridx = x + 1; gc.gridy = y; gc.gridwidth = 1;
        panel.add(field, gc);
    }

    private void addComponent(JPanel panel, Component comp, int x, int y, int w, int h) {
        if(gc == null) gc = new GridBagConstraints();
        gc.gridx = x; gc.gridy = y;
        gc.gridwidth = w; gc.gridheight = h;
        gc.weightx = 1.0; gc.weighty = 0.0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gc);
    }

    private void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan",
                    "root",
                    "reddy123"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed: " + e.getMessage());
            submit.setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submit) submitForm();
        else if(e.getSource() == clear) clearFields();
    }

    private void submitForm() {
        try {
            String progID = id.getText().trim();
            String progName = name.getText().trim();
            String progType = type.getText().trim();
            String progDura = dura.getText().trim();
            String progDept = dept.getText().trim();

            if(progID.isEmpty() || progName.isEmpty() || progType.isEmpty() || progDura.isEmpty() || progDept.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }

            String sql = "INSERT INTO programs (program_id, program_name, program_type, duration, department) VALUES (?, ?, ?, ?, ?)";
            pst = con.prepareStatement(sql);
            pst.setString(1, progID);
            pst.setString(2, progName);
            pst.setString(3, progType);
            pst.setString(4, progDura);
            pst.setString(5, progDept);

            int inserted = pst.executeUpdate();
            if(inserted>0) {
                JOptionPane.showMessageDialog(this,"Program Saved Successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,"Failed to Save Program.");
            }

        } catch (Exception ex){
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());
        }
    }

    private void clearFields() {
        id.setText(""); name.setText(""); type.setText(""); dura.setText(""); dept.setText("");
    }
}






