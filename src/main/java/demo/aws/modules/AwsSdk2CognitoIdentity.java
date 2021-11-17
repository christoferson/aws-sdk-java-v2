package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityException;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityDescription;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityPoolShortDescription;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsResponse;

public class AwsSdk2CognitoIdentity {

	private CognitoIdentityClient client;
	
	public AwsSdk2CognitoIdentity(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CognitoIdentityClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void identityPoolList() {

        try {
        	
        	ListIdentityPoolsRequest request = ListIdentityPoolsRequest.builder()
        			.maxResults(3)
        			.build();
        	ListIdentityPoolsResponse result = client.listIdentityPools(request);
            List<IdentityPoolShortDescription> list = result.identityPools();

            for (IdentityPoolShortDescription element : list) {
                System.out.println(String.format("%s %s", element.identityPoolId(), element.identityPoolName()));
            }

        } catch(CognitoIdentityException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
	}
	
	public void identityPoolListUsers(String poolId) {

        try {
        	
        	ListIdentitiesRequest request = ListIdentitiesRequest.builder()
        			.identityPoolId(poolId)
        			.maxResults(60)
        			.build();
        	ListIdentitiesResponse result = client.listIdentities(request);
            List<IdentityDescription> list = result.identities();

            for (IdentityDescription element : list) {
                System.out.println(String.format("%s %s", element.identityId(), element.getValueForField("name", String.class)));
            }

        } catch(CognitoIdentityException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
	}
	
	
}
