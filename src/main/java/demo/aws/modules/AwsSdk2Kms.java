package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.KmsException;
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
	
}
