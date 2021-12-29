package demo.aws.modules;

import java.util.List;
import java.util.Objects;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.CreateAliasResponse;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.KeyMetadata;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListAliasesRequest;
import software.amazon.awssdk.services.kms.model.ListAliasesResponse;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesRequest;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesResponse;
import software.amazon.awssdk.services.kms.model.ListKeysRequest;
import software.amazon.awssdk.services.kms.model.ListKeysResponse;

public class AwsSdk2Kms {

	private KmsClient client;
	
	public AwsSdk2Kms(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = KmsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
    public void keyList() throws KmsException {

    	System.out.printf("Listing Keys ... %n");
    	
        ListKeysRequest listKeysRequest = ListKeysRequest.builder()
                .limit(15)
                .build();

        ListKeysResponse keysResponse = client.listKeys(listKeysRequest);
        List<KeyListEntry> keyListEntries = keysResponse.keys();
        for (KeyListEntry key : keyListEntries) {
            System.out.println(String.format("%s %s", key.keyArn(), key.keyId()));
        }

    }	

    public void aliasList() throws KmsException {

    	System.out.printf("Listing Key Aliases ... %n");
    	
        ListAliasesRequest aliasesRequest = ListAliasesRequest.builder()
            .limit(15)
            .build();

        ListAliasesResponse aliasesResponse = client.listAliases(aliasesRequest) ;
        List<AliasListEntry> aliases = aliasesResponse.aliases();

        for (AliasListEntry alias: aliases) {
            System.out.println(String.format("%s %s", alias.aliasName(), alias.targetKeyId()));
        }

    }
    
    public void aliasList(String keyId) throws KmsException {

    	Objects.requireNonNull(keyId);
    	
    	System.out.printf("Listing Alias for Key = %s ... %n", keyId);
    	
        ListAliasesRequest aliasesRequest = ListAliasesRequest.builder()
            .limit(15)
            .keyId(keyId)
            .build();

        ListAliasesResponse aliasesResponse = client.listAliases(aliasesRequest) ;
        List<AliasListEntry> aliases = aliasesResponse.aliases();

        for (AliasListEntry alias: aliases) {
            System.out.println(String.format("%s", alias.aliasName()));
        }

    }
    
	public void aliasCreate(String aliasName, String targetKeyId) throws KmsException {

		CreateAliasRequest aliasRequest = CreateAliasRequest.builder()
				.aliasName(aliasName)
				.targetKeyId(targetKeyId)
				.build();

		CreateAliasResponse response = client.createAlias(aliasRequest);
		System.out.println(String.format("Created Alias: Alias=%s Key=%s Response=%s", aliasName, targetKeyId, response));
		
	}
    
	public void keyDescribe(String keyId) throws KmsException {

		Objects.requireNonNull(keyId);
		
		DescribeKeyRequest keyRequest = DescribeKeyRequest.builder().keyId(keyId).build();

		DescribeKeyResponse response = client.describeKey(keyRequest);
		KeyMetadata metadata = response.keyMetadata();
		System.out.println("Description: " + metadata.description());
		System.out.println("Key Arn: " + metadata.arn());
		System.out.printf("Key Spec: %s %n", metadata.keySpecAsString());
		System.out.printf("Key State: %s %n", metadata.keyStateAsString());

	}
	
	public void keyListPolicy(String keyId) throws KmsException {

		Objects.requireNonNull(keyId);

		ListKeyPoliciesRequest req = ListKeyPoliciesRequest.builder().keyId(keyId).build();
		ListKeyPoliciesResponse result = client.listKeyPolicies(req);
		for (String policyName : result.policyNames()) {
			System.out.printf("%s %n", policyName);
		}

	}
	
    public byte[] encryptData(String keyId, String data) throws KmsException {

        SdkBytes myBytes = SdkBytes.fromUtf8String(data);

        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(myBytes)
                .build();

        EncryptResponse response = client.encrypt(encryptRequest);
        String algorithm = response.encryptionAlgorithmAsString();
        System.out.println("Encryption algorithm: " + algorithm);

        SdkBytes encryptedData = response.ciphertextBlob();
        //System.out.println(Arrays.toString(encryptedData.asByteArray()));

        return encryptedData.asByteArray();

    }
    
	public String decryptData(String keyId, byte[] encryptedData) throws KmsException {
		
		SdkBytes sdkEncryptedData = SdkBytes.fromByteArray(encryptedData);

		DecryptRequest decryptRequest = DecryptRequest.builder()
				.ciphertextBlob(sdkEncryptedData)
				.keyId(keyId)
				.build();

		DecryptResponse decryptResponse = client.decrypt(decryptRequest);
		SdkBytes decryptedData = decryptResponse.plaintext();

		return decryptedData.asUtf8String();

	}
}
