package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GenerateCredentialReportResponse;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;

public class AwsSdk2Iam {

	private IamClient client;
	
	public AwsSdk2Iam(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = IamClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void callerIdentityGet() {

		System.out.println(String.format("Get CallerIdentity"));
		
//		GetCallerIdentityRequest request = GetCallerIdentityRequest.builder()
//				.build();

		GenerateCredentialReportResponse result = client.generateCredentialReport();
		System.out.println(String.format("result=%s", 
				result));

	}

	
	
}
