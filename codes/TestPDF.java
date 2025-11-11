import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

public class TestPDF {
    public static void main(String[] args) {
        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream("hello.pdf"));
            doc.open();
            doc.add(new Paragraph("Hello, iText is working!"));
            doc.close();
            System.out.println("PDF created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
