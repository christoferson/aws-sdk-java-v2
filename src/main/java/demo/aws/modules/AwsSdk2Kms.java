package demo.aws.modules;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.CreateAliasRequest;
import software.amazon.awssdk.services.kms.model.CreateAliasResponse;
import software.amazon.awssdk.services.kms.model.DataKeySpec;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.DeleteAliasRequest;
import software.amazon.awssdk.services.kms.model.DeleteAliasResponse;
import software.amazon.awssdk.services.kms.model.DescribeKeyRequest;
import software.amazon.awssdk.services.kms.model.DescribeKeyResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyResponse;
import software.amazon.awssdk.services.kms.model.GetKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.GetKeyPolicyResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.KeyMetadata;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListAliasesRequest;
import software.amazon.awssdk.services.kms.model.ListAliasesResponse;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesRequest;
import software.amazon.awssdk.services.kms.model.ListKeyPoliciesResponse;
import software.amazon.awssdk.services.kms.model.ListKeysRequest;
import software.amazon.awssdk.services.kms.model.ListKeysResponse;
import software.amazon.awssdk.services.kms.model.PutKeyPolicyRequest;
import software.amazon.awssdk.services.kms.model.PutKeyPolicyResponse;

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
            .limit(50)
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
            .limit(5)
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
	
	public void aliasDelete(String aliasName) throws KmsException {

		DeleteAliasRequest aliasRequest = DeleteAliasRequest.builder()
				.aliasName(aliasName)
				.build();

		DeleteAliasResponse response = client.deleteAlias(aliasRequest);
		System.out.println(String.format("Deleted Alias: Alias=%s Response=%s", aliasName, response));
		
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

	public void keyGetPolicy(String keyId, String policyName) throws KmsException {

		Objects.requireNonNull(keyId);

		GetKeyPolicyRequest req = GetKeyPolicyRequest.builder().keyId(keyId).policyName(policyName).build();
		GetKeyPolicyResponse result = client.getKeyPolicy(req);
		System.out.printf("%s %n", result.policy());

	}
	
	public void keyPutPolicy(String keyId, String policyName, String policy) throws KmsException {

		Objects.requireNonNull(keyId);

		PutKeyPolicyRequest req = PutKeyPolicyRequest.builder().keyId(keyId).policyName(policyName).policy(policy).build();
		PutKeyPolicyResponse result = client.putKeyPolicy(req);
		System.out.printf("%s %n", result);

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
	
	public GenerateDataKeyResponse generateDataKey(String keyId) throws KmsException {
		
        GenerateDataKeyRequest dataKeyRequest = GenerateDataKeyRequest.builder()
        		.keyId(keyId)
        		.keySpec(DataKeySpec.AES_256)
        		.build();

        GenerateDataKeyResponse dataKeyResult = client.generateDataKey(dataKeyRequest);

        SdkBytes plaintextKey = dataKeyResult.plaintext();

        SdkBytes encryptedKey = dataKeyResult.ciphertextBlob();
        
        System.out.printf(
            "Key(Encrypted): %s%n Key(Plain): %s%n",
            encryptedKey,
            plaintextKey
        );

		return dataKeyResult;

	}
	
    public String envelopeEncryptData(byte[] plainDataKeyByteArray, String data) {
    	byte[] encryptedByteArray = null;
    	String encryptedTextBase64 = null;
    	try {
    	    Cipher cipher = Cipher.getInstance("AES");
    	    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(plainDataKeyByteArray, "AES"));
    	    encryptedByteArray = cipher.doFinal(data.getBytes());
			encryptedTextBase64 = Base64.getEncoder().encodeToString(encryptedByteArray);
			System.out.printf(
		            "Data(Encrypted): %s%n",
		            encryptedTextBase64
		        );
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}

        return encryptedTextBase64;

    }
    
    public String envelopeDecryptData(String encryptedDataKeyBase64, String encryptedTextBase64) {
    	String plainText = null;
    	byte[] encryptedTextByteArray = Base64.getDecoder().decode(encryptedTextBase64);
    	byte[] encryptedDataKeyByteArray = Base64.getDecoder().decode(encryptedDataKeyBase64);
    	try {

    		DecryptRequest decryptRequest = DecryptRequest.builder()
    			    .ciphertextBlob(SdkBytes.fromByteArray(encryptedDataKeyByteArray))
    			    .build();
    		SdkBytes plainTextKey = client.decrypt(decryptRequest).plaintext();
    		
    	    Cipher cipher = Cipher.getInstance("AES");
    	    cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(plainTextKey.asByteArray(), "AES"));
    	    byte[] plainTextByteArray = cipher.doFinal(encryptedTextByteArray);
			plainText = new String(plainTextByteArray);

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}

        return plainText;

    }

}
