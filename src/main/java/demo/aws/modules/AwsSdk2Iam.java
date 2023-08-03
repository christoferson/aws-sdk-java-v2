package demo.aws.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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

		System.out.println(String.format("Generate CredentialReport"));
		
//		GetCallerIdentityRequest request = GetCallerIdentityRequest.builder()
//				.build();

		GenerateCredentialReportResponse result = client.generateCredentialReport();
		System.out.println(String.format("result=%s", 
				result));

	}

	public void getCredentialReport() {

		System.out.println(String.format("Get CredentialReport"));
		
		GetCredentialReportRequest request = GetCredentialReportRequest.builder()
				.build();

		GetCredentialReportResponse result = client.getCredentialReport(request);
		System.out.println(String.format("result=%s", result.generatedTime()));
		String content = result.content().asString(Charset.forName("UTF-8"));
		
		try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
			reader.lines().forEach(line -> {
				System.out.println(String.format("result.content=%s", line));
			});
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 

	}
	
	// UpdateAccessKey 
	
}
