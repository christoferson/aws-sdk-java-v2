package demo.aws.client.dax;

import software.amazon.dax.ClusterDaxAsyncClient;
import software.amazon.dax.Configuration;

import java.io.IOException;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;


public class AwsClientDaxAsync {

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

	
}
