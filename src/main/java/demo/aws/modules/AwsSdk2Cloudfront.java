package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsRequest;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;

public class AwsSdk2Cloudfront {

	private CloudFrontClient client;
	
	public AwsSdk2Cloudfront(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CloudFrontClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(Region.AWS_GLOBAL)
				  .build();
	}

	public void distributionList() {

		System.out.println(String.format("List Cloudfront Distribution"));
		
		ListDistributionsRequest request = ListDistributionsRequest.builder()
				.build();

		ListDistributionsResponse result = client.listDistributions(request);
		List<DistributionSummary> elements = result.distributionList().items();
		for (DistributionSummary element : elements) {
			System.out.println(String.format("ID=%s Comment=%s DomainName=%s", 
					element.id(), element.comment(), element.domainName()));
		}
	}
	
}
