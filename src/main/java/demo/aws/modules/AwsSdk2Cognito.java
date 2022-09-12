package demo.aws.modules;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	public Map<String, String> userPoolListUsers(String poolId, Integer limit) {

		Map<String, String> users = new TreeMap<>();
		
        try {
        	
        	ListUsersRequest request = ListUsersRequest.builder()
        			.userPoolId(poolId)
        			.limit(limit)
        			.build();
        	ListUsersResponse result = client.listUsers(request);
            List<UserType> list = result.users();

            for (UserType element : list) {
            	
                System.out.println(String.format("%s %s", element.username(), element.userStatus()));
                users.put(element.username(), element.userStatusAsString());
            }

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
        }

        return users;
        
	}
	
	public Map<String, String> userPoolListUsersAll(String poolId) {

		Map<String, String> users = new TreeMap<>();
		
		String paginationToken = null;
		boolean next = false;
		do {
        try {
        	
        	ListUsersRequest request = ListUsersRequest.builder()
        			.userPoolId(poolId)
        			.limit(60)
        			.paginationToken(paginationToken)
        			.build();
        	ListUsersResponse result = client.listUsers(request);
            List<UserType> list = result.users();

            for (UserType element : list) {
            	
                System.out.println(String.format("%s %s", element.username(), element.userStatus()));
                users.put(element.username(), element.userStatusAsString());
            }
            
            paginationToken = result.paginationToken();
            next = paginationToken != null;

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
        }
		} while (next);

        return users;
        
	}
	
	public String userPoolGetUser(String userPoolId, String name) throws CognitoIdentityProviderException {

		System.out.println(String.format("Get Cognito User. User=%s", name));
		
		AdminGetUserRequest userRequest = AdminGetUserRequest.builder()
				.userPoolId(userPoolId)
				.username(name)
				.build();

		AdminGetUserResponse response = client.adminGetUser(userRequest);
		
		System.out.println(String.format("[GetUser] User=%s Status=%s", response.username(), response.userStatus()));
		List<AttributeType> attributeList = response.userAttributes();
		for (AttributeType attribute : attributeList) {
			System.out.println(String.format("  %s=%s", attribute.name(), attribute.value()));
		}
		
		return response.username();

	}
	
	public void userPoolDeleteUser(String userPoolId, String name) {

		try {

			AdminDeleteUserRequest userRequest = AdminDeleteUserRequest.builder().userPoolId(userPoolId)
					.username(name)
					.build();

			client.adminDeleteUser(userRequest);
			System.out.println(String.format("[DeleteUser] User=%s deleted.", name));

		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}

	}
	
	public boolean userPoolNewUser(String userPoolId, String name, String email, String password) {

		boolean success = false;
		
		try {

			AttributeType userAttrs = AttributeType.builder().name("email").value(email).build();
			AttributeType userAttrs2 = AttributeType.builder().name("email_verified").value("true").build();

			AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder().userPoolId(userPoolId).username(name)
					.temporaryPassword(password)
					.userAttributes(userAttrs, userAttrs2)
					//.messageAction(MessageActionType.SUPPRESS)
					.build();

			AdminCreateUserResponse response = client.adminCreateUser(userRequest);
			System.out.println(String.format("[NewUser] User=%s Status=%s", response.user().username(), response.user().userStatus()));
			success = true;
		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
		
		return success;

	}
	
	public  void userPoolAddUserToGroup(String userPoolId, String name, String groupName) {
	
		try {

			AdminAddUserToGroupRequest userRequest = AdminAddUserToGroupRequest.builder().userPoolId(userPoolId)
					.username(name)
					.groupName(groupName)
					.build();

			client.adminAddUserToGroup(userRequest);
			System.out.println(String.format("[AddUserToGroup] User=%s added to Group=%s", name, groupName));

		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
	}

	public boolean userPoolResendMail(String userPoolId, String name, String password) {

		boolean success = false;
		
		try {

			//AttributeType userAttrs = AttributeType.builder().name("email").value(email).build();
			//AttributeType userAttrs2 = AttributeType.builder().name("email_verified").value("true").build();
			
			AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder().userPoolId(userPoolId)
					.username(name)
					.temporaryPassword(password)
					.messageAction(MessageActionType.RESEND)
					.desiredDeliveryMediums(DeliveryMediumType.EMAIL)
					.build();

			AdminCreateUserResponse response = client.adminCreateUser(userRequest);
			System.out.println(String.format("[ResendMail] User=%s Status=%s", response.user().username(), response.user().userStatus()));
			success = true;
		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
		
		return success;

	}
	
	
}
