import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class view_minor_subjects extends JPanel {

    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;

    public view_minor_subjects() {
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"S.No", "Subject Name", "Category", "Program Name", "Semester"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        scrollPane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
            String sql = "SELECT minor_subject_id, subject_name, category, program_name, semester FROM minor_subjects";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            int serial = 1;
            while (rs.next()) {
                Object[] row = {
                        serial++,
                        rs.getString("subject_name"),
                        rs.getString("category"),
                        rs.getString("program_name") != null ? rs.getString("program_name") : "N/A",
                        rs.getString("semester")
                };
                model.addRow(row);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading minor subjects: " + e.getMessage());
        }
    }
}
