package demo.aws.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AddCustomAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.MessageActionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

// Custom Attributes cannot be deleted or modified after adding to User Pool.
// You can delete Custom Attribute association from User
public class AwsSdk2Cognito {

	private CognitoIdentityProviderClient  client;
	
	public AwsSdk2Cognito(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CognitoIdentityProviderClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void userPoolList() {

     	ListUserPoolsRequest request = ListUserPoolsRequest.builder()
    			.maxResults(3)
    			.build();
     	
    	ListUserPoolsResponse result = client.listUserPools(request);
        List<UserPoolDescriptionType> list = result.userPools();

        for (UserPoolDescriptionType element : list) {
            System.out.println(String.format("%s %s", element.id(), element.name()));
        }
        
	}

	public void userPoolRegisterGroup(String poolId, String groupName, String description) {
		
		System.out.printf("Register Cognito Group. Pool=%s Group=%s %n", poolId, groupName);

		CreateGroupRequest request = CreateGroupRequest.builder()
    			.userPoolId(poolId)
    			.groupName(groupName)
    			.description(description)
    			.build();
    	
		CreateGroupResponse result = client.createGroup(request);
    	
		System.out.println(String.format("%s", result));
        
	}

	public Map<String, String> userPoolListGroups(String poolId, Integer limit) {
		
		System.out.printf("Listing Cognito Groups. Pool=%s %n", poolId);

		Map<String, String> users = new TreeMap<>();
		
    	ListGroupsRequest request = ListGroupsRequest.builder()
    			.userPoolId(poolId)
    			.limit(limit)
    			.build();
    	
    	ListGroupsResponse result = client.listGroups(request);
    	
        List<GroupType> list = result.groups();

        for (GroupType element : list) {
        	
            System.out.println(String.format("%s %s", element.groupName(), element.description()));
            users.put(element.groupName(), element.description());
        }

        return users;
        
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
	
	public static class AsicsCognitoUser {
		public String name;
		public String email;
		public String status;
		Map<String, String> attributes = Collections.emptyMap(); 
		@Override
		public String toString() {
			return String.format("%s, %s, %s %s]", name, email, status, attributes);
		}	
	}
	
	public Map<String, AsicsCognitoUser> userPoolListUsersAllV2(String poolId) {

		Map<String, AsicsCognitoUser> users = new TreeMap<>();
		
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
            	
            	AsicsCognitoUser muser = new AsicsCognitoUser();
            	muser.name = element.username();
            	muser.status = element.userStatusAsString();
            	
            	List<AttributeType> attributes = element.attributes();
            	Map<String, String> attributeMap = attributes.stream().collect(Collectors.toMap(AttributeType::name, AttributeType::value));
            	muser.attributes = attributeMap;
            	muser.email = attributeMap.get("email");
            	
                System.out.println(String.format("%s %s", element.username(), element.userStatus()));
                users.put(element.username(), muser);
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
	/*
	//addCustomAttributes
	public String userPoolEditUserAttribute(String userPoolId, String name) throws CognitoIdentityProviderException {

		System.out.println(String.format("Get Cognito User. User=%s", name));
		
		AddCustomAttributesRequest userRequest = AddCustomAttributesRequest.builder()
				.userPoolId(userPoolId)
				.username(name)
				.build();

		AdminGetUserResponse response = client.
		
		System.out.println(String.format("[GetUser] User=%s Status=%s", response.username(), response.userStatus()));
		List<AttributeType> attributeList = response.userAttributes();
		for (AttributeType attribute : attributeList) {
			System.out.println(String.format("  %s=%s", attribute.name(), attribute.value()));
		}
		
		return response.username();

	}
	*/
	
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
	
	public  void userPoolAddUserToGroup(String userPoolId, String userName, String groupName) {
		
		System.out.println(String.format("Add Cognito User to Group. User=%s Group=%s", userName, groupName));
	
		AdminAddUserToGroupRequest userRequest = AdminAddUserToGroupRequest.builder().userPoolId(userPoolId)
				.username(userName)
				.groupName(groupName)
				.build();

		client.adminAddUserToGroup(userRequest);
		System.out.println(String.format("[AddUserToGroup] User=%s added to Group=%s", userName, groupName));

	}

	public  void userPoolUserListGroup(String userPoolId, String userName) {
		
		System.out.println(String.format("List Cognito User Groups User=%s", userName));
	
		AdminListGroupsForUserRequest request = AdminListGroupsForUserRequest.builder()
				.userPoolId(userPoolId)
				.username(userName)
				.build();

		AdminListGroupsForUserResponse response = client.adminListGroupsForUser(request);
		List<GroupType> elements = response.groups();
		for (GroupType element : elements) {
			System.out.println(String.format("   Group=%s", element.groupName()));
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
	
	
	public boolean userPoolAdminConfirmSignUp(String userPoolId, String name) {

		boolean success = false;
		
		try {

			//AttributeType userAttrs = AttributeType.builder().name("email").value(email).build();
			//AttributeType userAttrs2 = AttributeType.builder().name("email_verified").value("true").build();
			
			AdminConfirmSignUpRequest request = AdminConfirmSignUpRequest.builder().userPoolId(userPoolId)
					.username(name)
					.userPoolId(userPoolId)
					.build();

			AdminConfirmSignUpResponse response = client.adminConfirmSignUp(request);
			System.out.println(String.format("[AdminConfirmSignUpResponse] %s", response));
			success = true;
		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
		
		return success;

	}
	
	public boolean userPoolAdminSetUserPassword(String userPoolId, String name, String password) {

		boolean success = false;
		
		try {

			//AttributeType userAttrs = AttributeType.builder().name("email").value(email).build();
			//AttributeType userAttrs2 = AttributeType.builder().name("email_verified").value("true").build();
			
			AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder().userPoolId(userPoolId)
					.username(name)
					.userPoolId(userPoolId)
					.password(password)
					.permanent(true)
					.build();

			AdminSetUserPasswordResponse response = client.adminSetUserPassword(request);
			System.out.println(String.format("[AdminSetUserPasswordResponse] %s", response));
			success = true;
		} catch (CognitoIdentityProviderException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
		}
		
		return success;

	}
	
	
}
