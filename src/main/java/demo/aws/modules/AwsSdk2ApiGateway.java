package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.ApiKey;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyRequest;
import software.amazon.awssdk.services.apigateway.model.GetApiKeyResponse;
import software.amazon.awssdk.services.apigateway.model.GetApiKeysRequest;
import software.amazon.awssdk.services.apigateway.model.GetApiKeysResponse;

public class AwsSdk2ApiGateway {

	private ApiGatewayClient client;
	
	public AwsSdk2ApiGateway(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = ApiGatewayClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	public void apiKeyList() {

		System.out.println(String.format("List ApiKey"));
		
		GetApiKeysRequest request = GetApiKeysRequest.builder()
				.build();

		GetApiKeysResponse result = client.getApiKeys(request);
		for (ApiKey apiKey : result.items()) {
			System.out.println(String.format("ID=%s Name=%s Description=%s CustomerId=%s", 
					apiKey.id(), apiKey.name(), apiKey.description(), apiKey.customerId()));
			for (String stageKey : apiKey.stageKeys()) {
				System.out.println(String.format("  %s", stageKey));
			}
		}

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
