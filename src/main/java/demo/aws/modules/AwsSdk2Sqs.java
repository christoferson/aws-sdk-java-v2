package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

public class AwsSdk2Sqs {

	private SqsClient client;
	
	public AwsSdk2Sqs(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SqsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
    public void queueList() {
    	
    	ListQueuesRequest request = ListQueuesRequest.builder()
                .build();

    	ListQueuesResponse response = client.listQueues(request);
        for (String url : response.queueUrls()) {
            System.out.println(String.format("%s", url));
        }

    }	
}
