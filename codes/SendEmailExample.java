import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class SendEmailExample {
    public static void main(String[] args) {
        // Sender and receiver details
        final String senderEmail = "reddysekharladdu@gmail.com";   // replace with your Gmail
        final String senderPassword = "kzzd pvro pdvp ocmq";  // replace with your Gmail App Password
        String recipientEmail = "mmukthananda577@gmail.com";       // replace with receiver

        // SMTP server configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject("Test Email - Jakarta Mail");
            message.setText("jai balayya !Padini minishal lo close ayye pabbu dhaggariki vellu !");

            // Send the email
            Transport.send(message);

            System.out.println("📧 Email vellidhi ra babu!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

