import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class stu_view extends JPanel implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JScrollPane scrollPane;
    JTextField searchField;
    JButton searchBtn, deleteBtn, refreshBtn, updateBtn, pdfBtn;
    JComboBox<String> filterBox;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public stu_view() {
        setLayout(new BorderLayout(10, 10));

        // Table Model with hidden ID column
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "ID", "S.No", "First Name", "Last Name", "Father Name", "Mother Name",
            "DOB", "Gender", "Email", "Phone", "Course", "Previous Qualification",
            "Aadhar", "Address"
        });

        // JTable
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Multi-line renderer
        TableCellRenderer multiLineRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JTextArea textArea = new JTextArea(value == null ? "" : value.toString());
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setFont(table.getFont());
                textArea.setOpaque(true);
                if (isSelected) {
                    textArea.setBackground(table.getSelectionBackground());
                    textArea.setForeground(table.getSelectionForeground());
                } else {
                    textArea.setBackground(table.getBackground());
                    textArea.setForeground(table.getForeground());
                }
                return textArea;
            }
        };

        int[] multiCols = {2, 3, 4, 5, 11, 13};
        for (int col : multiCols) {
            table.getColumnModel().getColumn(col).setCellRenderer(multiLineRenderer);
        }

        int[] widths = {0, 60, 180, 180, 220, 220, 120, 100, 220, 140, 120, 140, 140, 350};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(1400, 600));
        add(scrollPane, BorderLayout.CENTER);

        // Top Panel
        JPanel topPanel = new JPanel();

        searchField = new JTextField(15);
        searchBtn = new JButton("Search");
        deleteBtn = new JButton("Delete");
        refreshBtn = new JButton("Refresh");
        updateBtn = new JButton("Update");
        pdfBtn = new JButton("Download PDF");

        filterBox = new JComboBox<>();
        filterBox.addItem("All"); // Default option
        loadPrograms();

        topPanel.add(new JLabel("Search (ID/Name):"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(new JLabel("Filter by Course:"));
        topPanel.add(filterBox);
        topPanel.add(refreshBtn);
        topPanel.add(updateBtn);
        topPanel.add(deleteBtn);
        topPanel.add(pdfBtn);

        add(topPanel, BorderLayout.NORTH);

        // Action listeners
        searchBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        pdfBtn.addActionListener(this);
        filterBox.addActionListener(this);

        loadData("All");
    }

    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/codeathan", "root", "reddy123");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    private void loadPrograms() {
        connect();
        try {
            String sql = "SELECT program_name FROM programs";
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                filterBox.addItem(rs.getString("program_name"));
            }

            rs.close();
            pst.close();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading programs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadData(String course) {
        connect();
        try {
            model.setRowCount(0);
            String sql = "SELECT * FROM student_details";
            if (!course.equals("All")) {
                sql += " WHERE course=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, course);
            } else {
                pst = con.prepareStatement(sql);
            }
            rs = pst.executeQuery();
            int counter = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), // hidden ID
                        counter++,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("father_name"),
                        rs.getString("mother_name"),
                        rs.getDate("dob"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course"),
                        rs.getString("prev_qualification"),
                        rs.getString("aadhar"),
                        rs.getString("address")
                });
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void searchStudent(String keyword) {
        connect();
        try {
            model.setRowCount(0);
            String sql = "SELECT * FROM student_details WHERE id LIKE ? OR first_name LIKE ? OR last_name LIKE ?";
            pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            pst.setString(3, "%" + keyword + "%");

            rs = pst.executeQuery();
            int counter = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        counter++,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("father_name"),
                        rs.getString("mother_name"),
                        rs.getDate("dob"),
                        rs.getString("gender"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("course"),
                        rs.getString("prev_qualification"),
                        rs.getString("aadhar"),
                        rs.getString("address")
                });
            }
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int actualId = (int) model.getValueAt(row, 0); // hidden ID
        try {
            connect();
            pst = con.prepareStatement("DELETE FROM student_details WHERE id=?");
            pst.setInt(1, actualId);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Deleted successfully.");
                loadData("All");
            }
            pst.close();
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        int actualId = (int) model.getValueAt(row, 0); // hidden ID

        String firstName = (String) model.getValueAt(row, 2);
        String lastName = (String) model.getValueAt(row, 3);
        String email = (String) model.getValueAt(row, 8);
        String phone = (String) model.getValueAt(row, 9);
        String course = (String) model.getValueAt(row, 10);
        String prevQual = (String) model.getValueAt(row, 11);

        JTextField fnField = new JTextField(firstName);
        JTextField lnField = new JTextField(lastName);
        JTextField emailField = new JTextField(email);
        JTextField phoneField = new JTextField(phone);
        JTextField courseField = new JTextField(course);
        JComboBox<String> prevQualBox = new JComboBox<>(new String[]{"MPC", "BIPC", "CEC", "HEC"});
        prevQualBox.setSelectedItem(prevQual);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.add(new JLabel("First Name:")); panel.add(fnField);
        panel.add(new JLabel("Last Name:")); panel.add(lnField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Course:")); panel.add(courseField);
        panel.add(new JLabel("Previous Qualification:")); panel.add(prevQualBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Student", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                connect();
                pst = con.prepareStatement("UPDATE student_details SET first_name=?, last_name=?, email=?, phone=?, course=?, prev_qualification=? WHERE id=?");
                pst.setString(1, fnField.getText().trim());
                pst.setString(2, lnField.getText().trim());
                pst.setString(3, emailField.getText().trim());
                pst.setString(4, phoneField.getText().trim());
                pst.setString(5, courseField.getText().trim());
                pst.setString(6, prevQualBox.getSelectedItem().toString());
                pst.setInt(7, actualId);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Updated successfully.");
                loadData("All");
                pst.close();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void exportToPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("students.pdf"));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;

        String filePath = chooser.getSelectedFile().getAbsolutePath();
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("Student Details\n\n",
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            PdfPTable pdfTable = new PdfPTable(table.getColumnCount() - 1); // exclude hidden ID
            pdfTable.setWidthPercentage(100);

            for (int i = 1; i < table.getColumnCount(); i++) {
                pdfTable.addCell(new Phrase(model.getColumnName(i)));
            }
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 1; j < table.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    pdfTable.addCell(new Phrase(value == null ? "" : value.toString()));
                }
            }

            document.add(pdfTable);
            document.close();
            JOptionPane.showMessageDialog(this, "Exported to PDF: " + filePath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            searchStudent(searchField.getText().trim());
        } else if (e.getSource() == deleteBtn) {
            deleteStudent();
        } else if (e.getSource() == refreshBtn) {
            loadData("All");
        } else if (e.getSource() == updateBtn) {
            updateStudent();
        } else if (e.getSource() == pdfBtn) {
            exportToPDF();
        } else if (e.getSource() == filterBox) {
            String course = filterBox.getSelectedItem().toString();
            loadData(course);
        }
    }
}
