package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetGroupsResponse;
import software.amazon.awssdk.services.xray.model.GetServiceGraphRequest;
import software.amazon.awssdk.services.xray.model.GetServiceGraphResponse;
import software.amazon.awssdk.services.xray.model.GetTraceSummariesRequest;
import software.amazon.awssdk.services.xray.model.GetTraceSummariesResponse;
import software.amazon.awssdk.services.xray.model.GroupSummary;
import software.amazon.awssdk.services.xray.model.ResourceARNDetail;
import software.amazon.awssdk.services.xray.model.Service;
import software.amazon.awssdk.services.xray.model.TraceSummary;

public class AwsSdk2Xray {

	private XRayClient client;
	
	public AwsSdk2Xray(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = XRayClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void groupList() {

		GetGroupsResponse result = client.getGroups();
	    List<GroupSummary> list = result.groups();
	
	    for (GroupSummary element : list) {
	        System.out.println(String.format("%s", element.groupName()));
	    }
        
	}

	public void serviceGraphGet() {
		
		System.out.println(String.format("Get ServiceGraph"));
		
		GetServiceGraphRequest request = GetServiceGraphRequest.builder()
				.startTime(Instant.now().minus(3, ChronoUnit.HOURS))
				.endTime(Instant.now())
				.build();

		GetServiceGraphResponse result = client.getServiceGraph(request);
	    List<Service> list = result.services();
	
	    for (Service element : list) {
	        System.out.println(String.format("%s %s %s", element.name(), element.startTime(), element.endTime()));
	    }
        
	}
	
	public void traceSummaryGet() {
		
		System.out.println(String.format("Get TraceSummary"));
		
		GetTraceSummariesRequest request = GetTraceSummariesRequest.builder()
				.startTime(Instant.now().minus(3, ChronoUnit.HOURS))
				.endTime(Instant.now())
				.build();

		GetTraceSummariesResponse result = client.getTraceSummaries(request);
	    List<TraceSummary> list = result.traceSummaries();
	
	    for (TraceSummary element : list) {
	        System.out.println(String.format("%s", element));
	        for (ResourceARNDetail resource : element.resourceARNs()) {
	        	System.out.println(String.format("   %s", resource));
	        }
	    }
        
	}
	
	
}
