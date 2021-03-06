package demo.aws.modules;

import java.util.List;
import java.util.Map;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityResponse;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListDeadLetterSourceQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListDeadLetterSourceQueuesResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;
import software.amazon.awssdk.services.sqs.model.PurgeQueueResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

// https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_Operations.html
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
    
    public void queueCreate(String queueName, Map<String, String> attributes, Map<String, String> tags) {

    	System.out.println(String.format("Create Queue..."));
    	
    	CreateQueueRequest request = CreateQueueRequest.builder()
    			.queueName(queueName)
    			.attributesWithStrings(attributes)
    			.tags(tags)
                .build();

    	CreateQueueResponse response = client.createQueue(request);
        System.out.println(String.format("%s", response.queueUrl()));

    }
    
    public void queueDelete(String queueUrl) {

    	System.out.println(String.format("Delete Queue..."));
    	
    	DeleteQueueRequest request = DeleteQueueRequest.builder()
    			.queueUrl(queueUrl)
                .build();

    	DeleteQueueResponse response = client.deleteQueue(request);
        System.out.println(String.format("%s", response));

    }    
	
    public void queueUrlGet(String queueName) {

    	System.out.println(String.format("Get Queue URL..."));
    	
    	GetQueueUrlRequest request = GetQueueUrlRequest.builder()
    			//.queueOwnerAWSAccountId(null)
    			.queueName(queueName)
                .build();

    	GetQueueUrlResponse response = client.getQueueUrl(request);
        System.out.println(String.format("%s", response.queueUrl()));

    }
    
    public void queueGetAttributes(String queueUrl, List<String> attrubuteNameList) {

    	System.out.println(String.format("Get QueueAttributes..."));
    	
    	GetQueueAttributesRequest request = GetQueueAttributesRequest.builder()
    			.queueUrl(queueUrl)
    			.attributeNamesWithStrings(attrubuteNameList)
                .build();

    	GetQueueAttributesResponse response = client.getQueueAttributes(request);
        response.attributesAsStrings().forEach((k, v) -> {
        	System.out.println(String.format("   %s %s", k, v));
        });

    }    

    public void queueListDeadLetterQueues(String queueUrl) {

    	System.out.println(String.format("List DeadLetterQueues..."));
    	
    	ListDeadLetterSourceQueuesRequest request = ListDeadLetterSourceQueuesRequest.builder()
    			.queueUrl(queueUrl)
                .build();

    	ListDeadLetterSourceQueuesResponse response = client.listDeadLetterSourceQueues(request);
        response.queueUrls().forEach((url) -> {
        	System.out.println(String.format("   %s", url));
        });

    }  
    
    public void queuePurge(String queueUrl) {

    	System.out.println(String.format("Purge Queue..."));
    	
    	PurgeQueueRequest request = PurgeQueueRequest.builder()
    			.queueUrl(queueUrl)
                .build();

    	PurgeQueueResponse response = client.purgeQueue(request);

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
