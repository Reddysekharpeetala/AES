import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class stu_view1 extends JFrame implements ActionListener {
    JTable table;
    JButton downloadBtn;

    public stu_view1() {
        setTitle("Student View - PDF Export Example");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Table data
        String[] columns = {"ID", "Name", "Program"};
        Object[][] data = {
                {"101", "Ravi Kumar", "BSc"},
                {"102", "Anu Sharma", "BCom"},
                {"103", "Sekhar Reddy", "MCA"}
        };

        table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scroll = new JScrollPane(table);

        // Button
        downloadBtn = new JButton("Download PDF");
        downloadBtn.addActionListener(this);

        // Layout
        add(scroll, BorderLayout.CENTER);
        add(downloadBtn, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == downloadBtn) {
            exportTableToPDF();
        }
    }

    private void exportTableToPDF() {
        try {
            String filePath = "StudentDetails.pdf";  // file will be created in project folder

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Title
            Paragraph title = new Paragraph("Student Details\n\n",
                    new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Table
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());

            // Add table headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                pdfTable.addCell(new Phrase(table.getColumnName(i)));
            }

            // Add table rows
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    pdfTable.addCell(new Phrase(
                            table.getValueAt(row, col).toString()
                    ));
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this,
                    "PDF saved successfully at: " + filePath);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new stu_view1().setVisible(true));
    }
}
