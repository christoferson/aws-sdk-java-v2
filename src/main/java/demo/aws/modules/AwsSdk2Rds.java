package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.ListKeysRequest;
import software.amazon.awssdk.services.kms.model.ListKeysResponse;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.StartDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.StartDbInstanceResponse;
import software.amazon.awssdk.services.xray.XRayClient;
import software.amazon.awssdk.services.xray.model.GetGroupsResponse;
import software.amazon.awssdk.services.xray.model.GetServiceGraphRequest;
import software.amazon.awssdk.services.xray.model.GetServiceGraphResponse;
import software.amazon.awssdk.services.xray.model.GetTraceSummariesRequest;
import software.amazon.awssdk.services.xray.model.GetTraceSummariesResponse;
import software.amazon.awssdk.services.xray.model.GroupSummary;
import software.amazon.awssdk.services.xray.model.ResourceARNDetail;
import software.amazon.awssdk.services.xray.model.Service;
import software.amazon.awssdk.services.xray.model.TraceSummary;

public class AwsSdk2Rds {

	private RdsClient client;
	
	public AwsSdk2Rds(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = RdsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void instanceList() {

    	System.out.printf("List Database Instance ... %n");
    	
    	DescribeDbInstancesRequest request = DescribeDbInstancesRequest.builder()
                .build();

    	DescribeDbInstancesResponse response = client.describeDBInstances(request);
        List<DBInstance> elements = response.dbInstances();
        for (DBInstance element : elements) {
            System.out.println(String.format("Arn=%s Name=%s Engine=%s EngineVer=%s Endpoint=%s MultiAz=%s", element.dbInstanceArn(), element.dbName(), element.engine(), 
            		element.engineVersion(), element.endpoint().address(),
            		element.multiAZ()));
        }
        
	}
	
	public void instanceStart(String instanceIdentifier) {

    	System.out.printf("Start Database Instance ... %n");
    	
    	StartDbInstanceRequest request = StartDbInstanceRequest.builder()
    			.dbInstanceIdentifier(instanceIdentifier)
                .build();

    	StartDbInstanceResponse response = client.startDBInstance(request);
    	DBInstance element = response.dbInstance();
        System.out.println(String.format("Started Arn=%s Name=%s", element.dbInstanceArn(), element.dbName()));
        
	}	

	
}
