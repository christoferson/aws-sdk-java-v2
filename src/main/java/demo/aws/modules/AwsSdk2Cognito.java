package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;

public class AwsSdk2Cognito {

	private CognitoIdentityProviderClient  client;
	
	public AwsSdk2Cognito(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CognitoIdentityProviderClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void userPoolList() {

        try {
        	
        	ListUserPoolsRequest request = ListUserPoolsRequest.builder()
        			.maxResults(3)
        			.build();
        	ListUserPoolsResponse result = client.listUserPools(request);
            List<UserPoolDescriptionType> list = result.userPools();

            for (UserPoolDescriptionType element : list) {
                System.out.println(String.format("%s %s", element.id(), element.name()));
            }

        } catch(CognitoIdentityException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
	}
/*	
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
	*/
	
}
