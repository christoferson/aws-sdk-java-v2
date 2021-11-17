package demo.aws.modules;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

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

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
        }
        
	}

	public Set<String> userPoolListUsers(String poolId) {

		Set<String> users = new TreeSet<>();
		
        try {
        	
        	ListUsersRequest request = ListUsersRequest.builder()
        			.userPoolId(poolId)
        			.limit(60)
        			.build();
        	ListUsersResponse result = client.listUsers(request);
            List<UserType> list = result.users();

            for (UserType element : list) {
            	
                System.out.println(String.format("%s %s", element.username(), element.userStatus()));
                users.add(element.username());
            }

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
        }

        return users;
        
	}
	
	
}
