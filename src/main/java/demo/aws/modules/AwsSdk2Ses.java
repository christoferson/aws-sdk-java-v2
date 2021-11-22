package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;

public class AwsSdk2Ses {

	private SesClient client;
	
	public AwsSdk2Ses(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SesClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
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
	
}
