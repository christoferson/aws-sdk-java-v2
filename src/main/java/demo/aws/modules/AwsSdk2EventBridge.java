package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBus;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesResponse;

public class AwsSdk2EventBridge {

	private EventBridgeClient client;
	
	public AwsSdk2EventBridge(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = EventBridgeClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	public void describeEventBus(String name) {

		System.out.println(String.format("Describe EventBus. Name=%s", name));
		
		DescribeEventBusRequest request = DescribeEventBusRequest.builder()
				.name(name)
				.build();

		DescribeEventBusResponse result = client.describeEventBus(request);
		DescribeEventBusResponse element = result;
		System.out.println(String.format("Arn=%s Name=%s Policy=%s", 
				element.arn(), element.name(), element.policy()));
		
	}
	
	public void listEventBus() {

		System.out.println(String.format("List EventBus"));
		
		ListEventBusesRequest request = ListEventBusesRequest.builder()
				.build();

		ListEventBusesResponse result = client.listEventBuses(request);
		List<EventBus> elements = result.eventBuses();
		
		for (EventBus element : elements) {
			System.out.println(String.format("Arn=%s Name=%s Policy=%s", 
					element.arn(), element.name(), element.policy()));
		}
	}
	
}
