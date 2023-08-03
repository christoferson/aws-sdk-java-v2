package demo.aws.modules;

import java.nio.charset.Charset;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GenerateCredentialReportResponse;
import software.amazon.awssdk.services.iam.model.GetCredentialReportRequest;
import software.amazon.awssdk.services.iam.model.GetCredentialReportResponse;

public class AwsSdk2Iam {

	private IamClient client;
	
	public AwsSdk2Iam(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = IamClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void generateCredentialReport() {

		System.out.println(String.format("Get generateCredentialReport"));
		
//		GetCallerIdentityRequest request = GetCallerIdentityRequest.builder()
//				.build();

		GenerateCredentialReportResponse result = client.generateCredentialReport();
		System.out.println(String.format("result=%s", 
				result));

	}

	public void getCredentialReport() {

		System.out.println(String.format("Get GetCredentialReport"));
		
		GetCredentialReportRequest request = GetCredentialReportRequest.builder()
				
				.build();

		GetCredentialReportResponse result = client.getCredentialReport(request);
		System.out.println(String.format("result=%s", result.generatedTime()));
		String content = result.content().asString(Charset.forName("UTF-8"));
		System.out.println(String.format("result.content=%s", content));

	}
	
	// UpdateAccessKey 
	
}
