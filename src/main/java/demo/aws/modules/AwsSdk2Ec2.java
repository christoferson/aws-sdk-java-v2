package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ec2.model.Image;
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

public class AwsSdk2Ec2 {

	private Ec2Client client;
	
	public AwsSdk2Ec2(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = Ec2Client.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void imagesDescribe() {
		
		System.out.println(String.format("Describe Images"));
		
		DescribeImagesRequest request = DescribeImagesRequest.builder()
				.build();

		DescribeImagesResponse result = client.describeImages(request);
	    List<Image> list = result.images();
	
	    for (Image image : list) {
	        System.out.println(String.format("%s", image));
	    }
        
	}
	
	
}
