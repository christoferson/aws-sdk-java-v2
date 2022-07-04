package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetGroupsResponse;
import software.amazon.awssdk.services.xray.model.GroupSummary;

public class AwsSdk2Xray {

	private XRayClient client;
	
	public AwsSdk2Xray(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = XRayClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void groupList() {

        	
	//	ListUserPoolsRequest request = ListUserPoolsRequest.builder()
	//			.maxResults(3)
	//			.build();
		GetGroupsResponse result = client.getGroups();
	    List<GroupSummary> list = result.groups();
	
	    for (GroupSummary element : list) {
	        System.out.println(String.format("%s", element.groupName()));
	    }
        
	}

	
	
}
