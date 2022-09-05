package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CloudFrontOriginAccessIdentitySummary;
import software.amazon.awssdk.services.cloudfront.model.DistributionSummary;
import software.amazon.awssdk.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesRequest;
import software.amazon.awssdk.services.cloudfront.model.ListCloudFrontOriginAccessIdentitiesResponse;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsRequest;
import software.amazon.awssdk.services.cloudfront.model.ListDistributionsResponse;
import software.amazon.awssdk.services.cloudfront.model.ListOriginRequestPoliciesRequest;
import software.amazon.awssdk.services.cloudfront.model.ListOriginRequestPoliciesResponse;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicy;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicyConfig;
import software.amazon.awssdk.services.cloudfront.model.OriginRequestPolicySummary;

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
	
	public void originAccessIdentityList() {

		System.out.println(String.format("List CloudFrontOriginAccessIdentities"));
		
		ListCloudFrontOriginAccessIdentitiesRequest request = ListCloudFrontOriginAccessIdentitiesRequest.builder()
				.build();

		ListCloudFrontOriginAccessIdentitiesResponse result = client.listCloudFrontOriginAccessIdentities(request);
		List<CloudFrontOriginAccessIdentitySummary> elements = result.cloudFrontOriginAccessIdentityList().items();
		for (CloudFrontOriginAccessIdentitySummary element : elements) {
			System.out.println(String.format("ID=%s Comment=%s CanonicalUserId=%s", 
					element.id(), element.comment(), element.s3CanonicalUserId()));
		}

	}

	public void originRequestPolicyList() {

		System.out.println(String.format("List OriginRequestPoliciesRequest"));
		
		ListOriginRequestPoliciesRequest request = ListOriginRequestPoliciesRequest.builder()
				.build();

		ListOriginRequestPoliciesResponse result = client.listOriginRequestPolicies(request);
		List<OriginRequestPolicySummary> elements = result.originRequestPolicyList().items();
		for (OriginRequestPolicySummary element : elements) {
			OriginRequestPolicy originPolicy = element.originRequestPolicy();
			OriginRequestPolicyConfig originPolicyConfig = originPolicy.originRequestPolicyConfig();
			System.out.println(String.format("ID=%s Name=%s Comment=%s Config=%s", 
					originPolicy.id(), originPolicyConfig.name(), originPolicyConfig.comment(), originPolicyConfig));
		}
	}
}
