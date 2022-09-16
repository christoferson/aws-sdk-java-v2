package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontOriginAccessIdentitySummary;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.Invalidation;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;
import software.amazon.awssdk.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesRequest;
import software.amazon.awssdk.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesResponse;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsRequest;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;
import software.amazon.awssdk.services.cloudfront.model.ListOriginRequestPoliciesRequest;
import software.amazon.awssdk.services.cloudfront.model.ListOriginRequestPoliciesResponse;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicy;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicyConfig;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicySummary;
import software.amazon.awssdk.services.cloudfront.model.Paths;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeEventBusResponse;

public class AwsSdk2EventBridge {

	private EventBridgeClient client;
	
	public AwsSdk2EventBridge(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = EventBridgeClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	public void describeEventBus() {

		System.out.println(String.format("Describe EventBus"));
		
		DescribeEventBusRequest request = DescribeEventBusRequest.builder()
				.build();

		DescribeEventBusResponse result = client.describeEventBus(request);
		DescribeEventBusResponse element = result;
		System.out.println(String.format("Arn=%s Name=%s Policy=%s", 
				element.arn(), element.name(), element.policy()));
		
	}
	
}
