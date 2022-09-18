package demo.aws.modules;

import java.time.Instant;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBus;
import software.amazon.awssdk.services.eventbridge.model.EventSource;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListEventBusesResponse;
import software.amazon.awssdk.services.eventbridge.model.ListEventSourcesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListEventSourcesResponse;
import software.amazon.awssdk.services.eventbridge.model.ListRulesRequest;
import software.amazon.awssdk.services.eventbridge.model.ListRulesResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;
import software.amazon.awssdk.services.eventbridge.model.Rule;

public class AwsSdk2EventBridge {

	private EventBridgeClient client;
	
	public AwsSdk2EventBridge(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = EventBridgeClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	// Event Bus

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
	

	
	//
	
	// Event Rules
	
	public void listEventRules(String namePrefix, String eventBusName) {

		System.out.println(String.format("List EventRule"));
		
		ListRulesRequest request = ListRulesRequest.builder()
				.namePrefix(namePrefix)
				.eventBusName(eventBusName)
				.build();

		ListRulesResponse result = client.listRules(request);
		List<Rule> elements = result.rules();
		
		for (Rule element : elements) {
			System.out.println(String.format("Name=%s Description=%s Pattern=%s Managed=%s", 
					element.name(), element.description(), element.eventPattern(), element.managedBy()));
		}
	}	

	//
	
	public void listEventSources() {

		System.out.println(String.format("List EventSources"));
		
		ListEventSourcesRequest request = ListEventSourcesRequest.builder()
				//.namePrefix(namePrefix)
				.build();

		ListEventSourcesResponse result = client.listEventSources(request);
		List<EventSource> elements = result.eventSources();
		
		for (EventSource element : elements) {
			System.out.println(String.format("Name=%s State=%s CreatedBy=%s ExpirationTime=%s", 
					element.name(), element.stateAsString(), element.createdBy(), element.expirationTime()));
		}
	}

	
	//
	
	// Global Endpoints

	
	// Archives
	
	// Replays
	
	// APi Destinations / Connections
	
	
	// Schemas / Custom Schema Registry
	
	public void putEvents(String eventBusName) {

		System.out.println(String.format("Put Events"));
		
		PutEventsRequest request = PutEventsRequest.builder()
				.entries(PutEventsRequestEntry.builder()
						.eventBusName(eventBusName)
						.source("my.company.app")
						.detailType("user.registered")
						.detail("{\"foo\":\"bar\"}")
						.resources("userid") //optional
						.time(Instant.now()) //optional
						.build())
				.build();

		PutEventsResponse result = client.putEvents(request);
		List<PutEventsResultEntry> elements = result.entries();
		
		for (PutEventsResultEntry element : elements) {
			System.out.println(String.format("ID=%s Code=%s Message=%s", 
					element.eventId(), element.errorCode(), element.errorMessage()));
		}
	}
	
	
	
}
