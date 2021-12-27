package demo.aws.modules;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.AliasListEntry;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.ListAliasesRequest;
import software.amazon.awssdk.services.kms.model.ListAliasesResponse;
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

        ListAliasesRequest aliasesRequest = ListAliasesRequest.builder()
            .limit(15)
            .build();

        ListAliasesResponse aliasesResponse = client.listAliases(aliasesRequest) ;
        List<AliasListEntry> aliases = aliasesResponse.aliases();

        for (AliasListEntry alias: aliases) {
            System.out.println(String.format("%s %s", alias.aliasName(), alias.targetKeyId()));
        }

    }
    
    public SdkBytes encryptData(String keyId, String data) throws KmsException {

        SdkBytes myBytes = SdkBytes.fromUtf8String(data);

        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(myBytes)
                .build();

        EncryptResponse response = client.encrypt(encryptRequest);
        String algorithm = response.encryptionAlgorithm().toString();
        System.out.println("The encryption algorithm is " + algorithm);

        SdkBytes encryptedData = response.ciphertextBlob();
        System.out.println(Arrays.toString(encryptedData.asByteArray()));

        return encryptedData;

    }
}
