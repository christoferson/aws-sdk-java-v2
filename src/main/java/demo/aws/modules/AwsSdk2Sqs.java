package demo.aws.modules;

import java.util.List;



import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

// https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_Operations.html
// Delete Message
// GetQueueUrl m-queue
// ListQueues
// PurgeQueue
public class AwsSdk2Sqs {

	private SqsClient client;
	
	public AwsSdk2Sqs(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SqsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
    public void queueList() {

    	System.out.println(String.format("Listing Queues..."));
    	
    	ListQueuesRequest request = ListQueuesRequest.builder()
                .build();

    	ListQueuesResponse response = client.listQueues(request);
        for (String url : response.queueUrls()) {
            System.out.println(String.format("%s", url));
        }

    }
    
    public void messageReceiveLongPolling(String queueUrl) {
		
		System.out.println(String.format("Queue:%s Receiving Message (Long)...", queueUrl));
		
		ReceiveMessageRequest request = ReceiveMessageRequest.builder()
				.queueUrl(queueUrl)
				.maxNumberOfMessages(10)
				//When the wait time for the ReceiveMessage API action is greater than 0, long polling is in effect. 
				//The maximum long polling wait time is 20 seconds.
				.waitTimeSeconds(20)
				.visibilityTimeout(30)
				.build();
		
		List<Message> messages = client.receiveMessage(request).messages();
		
		for (Message m : messages) {
			System.out.println(String.format("Queue:%s Received Message: %s", queueUrl, m));
			System.out.println("   " + m.body());
			System.out.println("   " + m.attributesAsStrings());
		}
		// Delete Messages after Receipt
		for (Message m : messages) {
			DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
					.queueUrl(queueUrl)
					.receiptHandle(m.receiptHandle())
					.build();
			client.deleteMessage(deleteRequest);
		}
		
	}
    
    public void messageVisibilityTimeoutChange(String queueUrl, String messageReceiptHandle, Integer visibilityTimeout) {
		
		System.out.println(String.format("Queue:%s Message:%s Change MessageVisibility (Integer)...", queueUrl, messageReceiptHandle));
		
		ChangeMessageVisibilityRequest request = ChangeMessageVisibilityRequest.builder()
				.queueUrl(queueUrl)
				.receiptHandle(messageReceiptHandle)
				.visibilityTimeout(visibilityTimeout)
				.build();
		
		ChangeMessageVisibilityResponse response = client.changeMessageVisibility(request);
		
	}

    public void messageDelete(String queueUrl, String messageReceiptHandle) {

		System.out.println(String.format("Queue:%s Message:%s Delete Message...", queueUrl, messageReceiptHandle));
		
		DeleteMessageRequest request = DeleteMessageRequest.builder()
				.queueUrl(queueUrl)
				.receiptHandle(messageReceiptHandle)
				.build();
		
		DeleteMessageResponse response = client.deleteMessage(request);

	}

}
