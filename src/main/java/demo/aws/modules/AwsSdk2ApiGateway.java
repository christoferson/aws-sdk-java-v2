package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyRequest;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyResponse;
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

public class AwsSdk2ApiGateway {

	private ApiGatewayClient client;
	
	public AwsSdk2ApiGateway(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = ApiGatewayClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	public void apiKeyGet(String keyId) {
		
		System.out.println(String.format("Get ApiKey"));
		
		GetApiKeyRequest request = GetApiKeyRequest.builder()
				.apiKey(keyId)
				.build();

		GetApiKeyResponse result = client.getApiKey(request);
		System.out.println(String.format("Name=%s Description=%s CustomerId=%s", result.name(), result.description(), result.customerId()));
		
		for (String stageKey : result.stageKeys()) {
			System.out.println(String.format("  %s", stageKey));
		}

	}
	
	
}
