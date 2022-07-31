package demo.aws.modules;

import java.util.List;
import java.util.Objects;


import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretListEntry;

public class AwsSdk2SecretsManager {

	private SecretsManagerClient client;
	
	public AwsSdk2SecretsManager(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SecretsManagerClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
    public void secretList() {

    	System.out.printf("Listing Secrets ... %n");
    	
    	ListSecretsRequest request = ListSecretsRequest.builder()
                .build();

    	ListSecretsResponse response = client.listSecrets(request);
        List<SecretListEntry> list = response.secretList();
        for (SecretListEntry element : list) {
            System.out.println(String.format("%s %s", element.arn(), element.description()));
        }

    }	

    public void secretDescribe(String secretId) {

    	System.out.printf("Describe Secret ... %n");
    	
    	DescribeSecretRequest request = DescribeSecretRequest.builder()
    			.secretId(secretId)
                .build();

    	DescribeSecretResponse response = client.describeSecret(request);
        System.out.println(String.format("%s %s", response.name(), response.description()));

    }	
}
