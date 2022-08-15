package demo.aws.client.dax;

import software.amazon.dax.ClusterDaxAsyncClient;
import software.amazon.dax.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;


public class AwsClientDaxAsync {

    private static AttributeValue attr(String s) {
        return AttributeValue.builder().s(s).build();
    }
    
	private DynamoDbAsyncClient client;
	
	public AwsClientDaxAsync(AwsCredentialsProvider credentialsProvider, Region region, String daxUrl) {
		
		try {
			this.client = ClusterDaxAsyncClient.builder()
					.overrideConfiguration(Configuration.builder()
							.credentialsProvider(credentialsProvider)
							.region(region)
							.url(daxUrl).build())
					.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void itemGet(String tableName, 
			String partitionKeyName, String partitionKey, 
			String sortKeyName, String sortKey) {
		
		try {
			CompletableFuture<GetItemResponse> response = client.getItem(GetItemRequest.builder()
			        .tableName(tableName)
			        .key(Map.of(partitionKeyName, attr(partitionKey), sortKeyName, attr(sortKey)))
			        .build());
			
			response.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
