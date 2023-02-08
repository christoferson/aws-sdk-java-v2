package demo.aws.tools;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class AwsSesSmtpClient {
	
	private Session session;

	public AwsSesSmtpClient(String username, String password, String smtpEndpoint) {
		
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
		Objects.requireNonNull(smtpEndpoint);

		this.session = createSession(username, password, smtpEndpoint);

	}

	public void sendMessage(String from, String to, String subject, String body) {
		
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(body, "text/html");
	
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
	
			message.setContent(multipart);
	
			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		System.out.println("Message Sent");

	}

	private static Session createSession(String username, String password, String smtpEndpoint) {
		Properties prop = new Properties();
		prop.put("mail.transport.protocol", "smtp");
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", smtpEndpoint);
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		});
		return session;
	}

}
