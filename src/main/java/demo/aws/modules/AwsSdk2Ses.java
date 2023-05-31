package demo.aws.modules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.CreateTemplateRequest;
import software.amazon.awssdk.services.ses.model.CreateTemplateResponse;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.GetTemplateRequest;
import software.amazon.awssdk.services.ses.model.GetTemplateResponse;
import software.amazon.awssdk.services.ses.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.ses.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.ses.model.ListTemplatesRequest;
import software.amazon.awssdk.services.ses.model.ListTemplatesResponse;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SendTemplatedEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.Template;
import software.amazon.awssdk.services.ses.model.TemplateMetadata;
import software.amazon.awssdk.services.ses.model.UpdateTemplateRequest;
import software.amazon.awssdk.services.ses.model.UpdateTemplateResponse;

public class AwsSdk2Ses {

	private SesClient client;
	
	public SesClient client() {
		return this.client;
	}
	
	public AwsSdk2Ses(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SesClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void identityList() {
		
		System.out.println("Listing Identities");
		
		ListIdentitiesRequest request = ListIdentitiesRequest.builder()
				.build();
		ListIdentitiesResponse response = client.listIdentities(request);
		List<String> elements = response.identities();
		for (String element : elements) {
			System.out.println(element);
		}
	}
	
	public void sendSimple(String sender, String recipient) {
		software.amazon.awssdk.services.ses.model.Message message = software.amazon.awssdk.services.ses.model.Message.builder()
				.subject(Content.builder().data("Subject" + System.currentTimeMillis()).build())
				.body(Body.builder().html(Content.builder().data("<html><body>Body" + System.currentTimeMillis() + "</body></html>").build()).build())
				.build();
		SendEmailRequest req = SendEmailRequest.builder()              
                .destination(Destination.builder().toAddresses(recipient).build())
                .source(sender)
                .returnPath(sender)
                .message(message)
                .build();
		SendEmailResponse response = this.client.sendEmail(req);
		
		System.out.println("sent " + response.messageId());
	}
	
    public  void sendRawEmail(String sender, String recipient) throws IOException {


        String subject = "Test Subject";

        // The email body for non-HTML email clients
        String bodyText = "Hello,\r\n" + "See the list of customers. ";

        // The HTML body of the email
        String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                + "<p> See the list of customers.</p>" + "</body>" + "</html>";

	    try {
	         sendRawEmail(client, sender, recipient, subject, bodyText, bodyHTML);
	         client.close();
	         System.out.println("Done");
	
	    } catch (IOException | MessagingException e) {
	        e.getStackTrace();
	    }


   }
    

    public  void sendRawEmail(SesClient client,
                            String sender,
                            String recipient,
                            String subject,
                            String bodyText,
                            String bodyHTML
                            ) throws AddressException, MessagingException, IOException {

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // Add subject, from and to lines
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

        // Create a multipart/alternative child container
        MimeMultipart msgBody = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain; charset=UTF-8");

        // Define the HTML part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container
        msgBody.addBodyPart(textPart);
        msgBody.addBodyPart(htmlPart);

        // Add the child container to the wrapper object
        wrap.setContent(msgBody);

        // Create a multipart/mixed parent container
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message
        message.setContent(msg);

        // Add the multipart/alternative part to the message
        msg.addBodyPart(wrap);

        try {
            System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             message.writeTo(outputStream);
             ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

             byte[] arr = new byte[buf.remaining()];
             buf.get(arr);

             SdkBytes data = SdkBytes.fromByteArray(arr);
             RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

             SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

             client.sendRawEmail(rawEmailRequest);

         } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
         }

    }
    

    public  void sendTemplatedEmail(SesClient client,
                            String sender,
                            String recipient,
                            String templateName, String templateData
                            ) {
    	
    	Destination destination = Destination.builder()
                .toAddresses(recipient)
                .build();
        
        try {
            System.out.println("Attempting to send an templated email through Amazon SES " + "using the AWS SDK for Java...");

        
             SendTemplatedEmailRequest rawEmailRequest = SendTemplatedEmailRequest.builder()
            		 //.configurationSetName(bodyHTML)
            		 .template(templateName)
            		 .templateData(templateData)
            		 .destination(destination)
            		 .source(sender)
                    .build();

             client.sendTemplatedEmail(rawEmailRequest);
             
             System.out.println("Sent");

         } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
         }

    }
    
    //
    
	public void templatesList() {
		
		System.out.println("Listing Templates");
		
		ListTemplatesRequest request = ListTemplatesRequest.builder()
				.build();
		ListTemplatesResponse response = client.listTemplates(request);
		List<TemplateMetadata> elements = response.templatesMetadata();
		for (TemplateMetadata element : elements) {
			System.out.println(element);
		}
	}

	public void templatesRegister() {
		
		System.out.println("Register Template");
		
		CreateTemplateRequest request = CreateTemplateRequest.builder()
				.template(Template.builder()
						.htmlPart("<b>Foo</b>")
						.subjectPart("Foo Subject")
						.templateName("MyTemplate" + System.currentTimeMillis())
						.build())
				.build();
		CreateTemplateResponse response = client.createTemplate(request);
		System.out.println(response);
		
	}
	
	public void templatesUpdate(String templateName) {
		
		System.out.println("Update Template " + templateName);
		
		UpdateTemplateRequest request = UpdateTemplateRequest.builder()
				.template(Template.builder()
						.templateName(templateName)
						.subjectPart("Foo Subject" + System.currentTimeMillis())
						.htmlPart(String.format("<b>Foo-%s</b>", System.currentTimeMillis()))
						.textPart(String.format("Foo-%s", System.currentTimeMillis()))
						.build())
				.build();
		UpdateTemplateResponse response = client.updateTemplate(request);
		System.out.println(response);
		
	}

	
}
