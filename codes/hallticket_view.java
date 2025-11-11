import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class hallticket_view extends JPanel implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JComboBox<String> programCombo;
    JButton generateBtn, refreshBtn, searchBtn, pdfBtn;
    JTextField searchField;

    public hallticket_view() {
        setLayout(new BorderLayout(10, 10));

        // --- Top panel for program selection and actions ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        programCombo = new JComboBox<>();
        generateBtn = new JButton("Generate Hall Ticket");
        refreshBtn = new JButton("Refresh");
        searchField = new JTextField(15);
        searchBtn = new JButton("Search");
        pdfBtn = new JButton("Download PDF");

        topPanel.add(new JLabel("Select Program:"));
        topPanel.add(programCombo);
        topPanel.add(generateBtn);
        topPanel.add(refreshBtn);
        topPanel.add(new JLabel("Search Name:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(pdfBtn);

        add(topPanel, BorderLayout.NORTH);

        // --- Table for students and hall tickets ---
        String[] columns = {"S.No", "Student Name", "Program Name", "Hall Ticket No"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        table.setDefaultEditor(Object.class, null); // non-editable

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Load program names into combo
        loadPrograms();

        // Button listeners
        generateBtn.addActionListener(this);
        refreshBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        pdfBtn.addActionListener(e -> exportToPDF());
        programCombo.addActionListener(this);

        // Load initial data
        loadData();
    }

    // Load distinct program names from student_details table
    public void loadPrograms() {
        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {
            programCombo.removeAllItems();
            String sql = "SELECT DISTINCT course FROM student_details";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                programCombo.addItem(rs.getString("course"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading programs: " + ex.getMessage());
        }
    }

    // Load students and their hall tickets
    public void loadData() {
        String selectedProgram = (String) programCombo.getSelectedItem();
        if (selectedProgram == null) return;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {
            model.setRowCount(0);

            String sql = "SELECT s.id, s.first_name, s.last_name, h.hall_ticket " +
                    "FROM student_details s " +
                    "LEFT JOIN halltickets h ON s.id = h.stu_id " +
                    "WHERE s.course = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, selectedProgram);
            ResultSet rs = pst.executeQuery();

            int counter = 1;
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                String hallTicket = rs.getString("hall_ticket") != null ? rs.getString("hall_ticket") : "N/A";

                Object[] row = {counter++, fullName, selectedProgram, hallTicket};
                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }

    // Search students by first or last name
    public void searchData() {
        String selectedProgram = (String) programCombo.getSelectedItem();
        String keyword = searchField.getText().trim();
        if (selectedProgram == null || keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a program and enter search text!");
            return;
        }

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            model.setRowCount(0);
            String sql = "SELECT s.id, s.first_name, s.last_name, h.hall_ticket " +
                    "FROM student_details s " +
                    "LEFT JOIN halltickets h ON s.id = h.stu_id " +
                    "WHERE s.course = ? AND (s.first_name LIKE ? OR s.last_name LIKE ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, selectedProgram);
            pst.setString(2, "%" + keyword + "%");
            pst.setString(3, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            int counter = 1;
            while (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                String hallTicket = rs.getString("hall_ticket") != null ? rs.getString("hall_ticket") : "N/A";

                Object[] row = {counter++, fullName, selectedProgram, hallTicket};
                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching students: " + ex.getMessage());
        }
    }

    // Generate hall tickets for students without one
    public void generateHallTickets() {
        String selectedProgram = (String) programCombo.getSelectedItem();
        if (selectedProgram == null) return;

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/codeathan", "root", "reddy123")) {

            // Get last hall ticket number for this program
            String lastTicketSql = "SELECT hall_ticket FROM halltickets WHERE program_name=? ORDER BY id DESC LIMIT 1";
            PreparedStatement lastPst = con.prepareStatement(lastTicketSql);
            lastPst.setString(1, selectedProgram);
            ResultSet lastRs = lastPst.executeQuery();

            int startNo = getProgramStartNumber(selectedProgram);
            if (lastRs.next()) {
                String lastTicket = lastRs.getString("hall_ticket");
                String numberPart = lastTicket.replaceAll("\\D", "");
                startNo = Integer.parseInt(numberPart) + 1;
            }

            // Students without hall tickets
            String selectSql = "SELECT s.id FROM student_details s " +
                    "LEFT JOIN halltickets h ON s.id = h.stu_id " +
                    "WHERE s.course=? AND h.hall_ticket IS NULL";
            PreparedStatement selectPst = con.prepareStatement(selectSql);
            selectPst.setString(1, selectedProgram);
            ResultSet rs = selectPst.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("id");
                String hallTicket = selectedProgram.substring(0, 3).toUpperCase() + startNo++;

                String insertSql = "INSERT INTO halltickets(stu_id, hall_ticket, program_name) VALUES (?, ?, ?)";
                PreparedStatement insertPst = con.prepareStatement(insertSql);
                insertPst.setInt(1, studentId);
                insertPst.setString(2, hallTicket);
                insertPst.setString(3, selectedProgram);
                insertPst.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Hall tickets generated successfully!");
            loadData();

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Some hall tickets already exist. Duplicate avoided.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating hall tickets: " + ex.getMessage());
        }
    }

    // Program-specific start number
    private int getProgramStartNumber(String program) {
        return switch (program.toUpperCase()) {
            case "BCA" -> 210001;
            case "BCOM" -> 220001;
            case "BSC" -> 230001;
            default -> 240001;
        };
    }

    // Export JTable data to PDF with file chooser
    private void exportToPDF() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setSelectedFile(new java.io.File("HallTickets.pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Title
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                        com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                Paragraph title = new Paragraph("Hall Ticket List", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Table
                PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Column headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(model.getColumnName(i)));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(cell);
                }

                // Rows
                for (int r = 0; r < model.getRowCount(); r++) {
                    for (int c = 0; c < model.getColumnCount(); c++) {
                        pdfTable.addCell(model.getValueAt(r, c).toString());
                    }
                }

                document.add(pdfTable);
                document.close();

                JOptionPane.showMessageDialog(this, "PDF saved as " + fileToSave.getAbsolutePath());

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == generateBtn) {
            generateHallTickets();
        } else if (src == refreshBtn || src == programCombo) {
            loadData();
        } else if (src == searchBtn) {
            searchData();
        }
    }
}
