package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.ApiKey;
import software.amazon.awssdk.services.apigateway.model.ApiStage;
import software.amazon.awssdk.services.apigateway.model.CreateApiKeyRequest;
import software.amazon.awssdk.services.apigateway.model.CreateApiKeyResponse;
import software.amazon.awssdk.services.apigateway.model.CreateUsagePlanKeyRequest;
import software.amazon.awssdk.services.apigateway.model.CreateUsagePlanKeyResponse;
import software.amazon.awssdk.services.apigateway.model.GetAccountRequest;
import software.amazon.awssdk.services.apigateway.model.GetAccountResponse;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyRequest;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyResponse;
import software.amazon.awssdk.services.apigateway.model.GetApiKeysRequest;
import software.amazon.awssdk.services.apigateway.model.GetApiKeysResponse;
import software.amazon.awssdk.services.apigateway.model.GetRestApiRequest;
import software.amazon.awssdk.services.apigateway.model.GetRestApiResponse;
import software.amazon.awssdk.services.apigateway.model.GetRestApisRequest;
import software.amazon.awssdk.services.apigateway.model.GetRestApisResponse;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanKeysRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanKeysResponse;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanResponse;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlansRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlansResponse;
import software.amazon.awssdk.services.apigateway.model.RestApi;
import software.amazon.awssdk.services.apigateway.model.UsagePlan;
import software.amazon.awssdk.services.apigateway.model.UsagePlanKey;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;

public class AwsSdk2Sts {

	private StsClient client;
	
	public AwsSdk2Sts(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = StsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void callerIdentityGet() {

		System.out.println(String.format("Get CallerIdentity"));
		
		GetCallerIdentityRequest request = GetCallerIdentityRequest.builder()
				.build();

		GetCallerIdentityResponse result = client.getCallerIdentity(request);
		System.out.println(String.format("Arn=%s", 
				result.arn()));

	}

	public void assumeRole(String roleArn) {

		System.out.println(String.format("Assume Role"));
		
		AssumeRoleRequest request = AssumeRoleRequest.builder()
				.roleArn(roleArn)
				.roleSessionName(String.valueOf(System.currentTimeMillis()))
				.build();

		AssumeRoleResponse result = client.assumeRole(request);
		Credentials credentials = result.credentials();
		System.out.println(String.format("Result=%s", result));
		System.out.println(String.format("AccessKey=%s", credentials.accessKeyId()));
		System.out.println(String.format("Secret=%s", credentials.secretAccessKey()));
		System.out.println(String.format("SessionToken=%s", credentials.sessionToken()));
		System.out.println(String.format("Expiration=%s", credentials.expiration()));
		//AwsSessionCredentials session = AwsSessionCredentials.create(credentials.accessKeyId(), credentials.secretAccessKey(), credentials.sessionToken());
	}
	
}
