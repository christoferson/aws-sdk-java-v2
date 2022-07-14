package demo.aws.modules;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

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
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanKeysRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanKeysResponse;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlanResponse;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlansRequest;
import software.amazon.awssdk.services.apigateway.model.GetUsagePlansResponse;
import software.amazon.awssdk.services.apigateway.model.UsagePlan;
import software.amazon.awssdk.services.apigateway.model.UsagePlanKey;

// https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/apigateway/ApiGatewayClient.html#createUsagePlanKey--
// https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-api-usage-plans.html?icmpid=docs_apigateway_console
// A usage plan specifies who can access one or more deployed API stages and methods—and optionally sets the target request rate to start throttling requests.
//  The plan uses API keys to identify API clients and who can access the associated API stages for each key.
// API keys are alphanumeric string values that you distribute to application developer customers to grant access to your API. 
// An API key can be associated with more than one usage plan. A usage plan can be associated with more than one stage. 
//  However, a given API key can only be associated with one usage plan for each stage of your API.
// 	createUsagePlanKey(CreateUsagePlanKeyRequest createUsagePlanKeyRequest)
// updateUsagePlan(UpdateUsagePlanRequest updateUsagePlanRequest)
public class AwsSdk2ApiGateway {

	private ApiGatewayClient client;
	
	public AwsSdk2ApiGateway(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = ApiGatewayClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}

	public void accountGet() {

		System.out.println(String.format("Get Account"));
		
		GetAccountRequest request = GetAccountRequest.builder()
				.build();

		GetAccountResponse result = client.getAccount(request);
		System.out.println(String.format("ApiKeyVersion=%s %nCloudwatchRoleArn=%s %nThrottleSettings=%s", 
				result.apiKeyVersion(), result.cloudwatchRoleArn(), result.throttleSettings()));
		

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
	
	
	public void apiKeyCreate(String name, String description) {

		System.out.println(String.format("Create ApiKey"));
		
		CreateApiKeyRequest request = CreateApiKeyRequest.builder()
				.name(name)
				.description(description)
				.build();

		CreateApiKeyResponse result = client.createApiKey(request);
		CreateApiKeyResponse apiKey = result;
		System.out.println(String.format("ID=%s Name=%s Description=%s CustomerId=%s", 
				apiKey.id(), apiKey.name(), apiKey.description(), apiKey.customerId()));

	}
	
	public void usagePlanList() {

		System.out.println(String.format("List UsagePlan"));
		
		GetUsagePlansRequest request = GetUsagePlansRequest.builder()
				.build();

		GetUsagePlansResponse result = client.getUsagePlans(request);
		for (UsagePlan usagePlan : result.items()) {
			System.out.println(String.format("ID=%s Name=%s Description=%s %n  Throttle=%s %n  Quota=%s", 
					usagePlan.id(), usagePlan.name(), usagePlan.description(), usagePlan.throttle(), usagePlan.quota()));
			
			for (ApiStage apiStage : usagePlan.apiStages()) {
				System.out.println(String.format("  %s", apiStage));
			}
			
		}

	}
	
	public void usagePlanGet(String usagePlanId) {

		System.out.println(String.format("Get UsagePlan - ID=%s", usagePlanId));
		
		GetUsagePlanRequest request = GetUsagePlanRequest.builder()
				.usagePlanId(usagePlanId)
				.build();

		GetUsagePlanResponse result = client.getUsagePlan(request);
		GetUsagePlanResponse usagePlan = result;
		
		System.out.println(String.format("ID=%s Name=%s Description=%s %n  Throttle=%s %n  Quota=%s", 
				usagePlan.id(), usagePlan.name(), usagePlan.description(), usagePlan.throttle(), usagePlan.quota()));
		
		for (ApiStage apiStage : usagePlan.apiStages()) {
			System.out.println(String.format("    %s", apiStage));
		}

	}
	
	public void usagePlanKeyList(String usagePlanId) {

		System.out.println(String.format("List UsagePlanKeys"));
		
		GetUsagePlanKeysRequest request = GetUsagePlanKeysRequest.builder()
				.usagePlanId(usagePlanId)
				.build();

		GetUsagePlanKeysResponse result = client.getUsagePlanKeys(request);
		for (UsagePlanKey usagePlanKey : result.items()) {
			System.out.println(String.format("ID=%s Name=%s Value=%s Type=%s", 
					usagePlanKey.id(), usagePlanKey.name(), usagePlanKey.value(), usagePlanKey.type()));
			
		}

	}
	

	
	public void usagePlanKeyCreate(String usagePlanId, String keyId) { //Add existing key to usage plan

		System.out.println(String.format("Create UsagePlanKey"));
		
		CreateUsagePlanKeyRequest request = CreateUsagePlanKeyRequest.builder()
				.usagePlanId(usagePlanId)
				.keyId(keyId)
				.keyType("API_KEY")
				.build();

		CreateUsagePlanKeyResponse result = client.createUsagePlanKey(request);
		
		CreateUsagePlanKeyResponse usagePlanKey = result;
		System.out.println(String.format("ID=%s Name=%s Value=%s Type=%s", 
				usagePlanKey.id(), usagePlanKey.name(), usagePlanKey.value(), usagePlanKey.type()));
			

	}
	
}
