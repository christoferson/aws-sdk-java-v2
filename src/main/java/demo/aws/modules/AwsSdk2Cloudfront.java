package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.cloudfront.CloudFrontClient;

public class AwsSdk2Cloudfront {

	private CloudFrontClient client;
	
	public AwsSdk2Cloudfront(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CloudFrontClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	
}
