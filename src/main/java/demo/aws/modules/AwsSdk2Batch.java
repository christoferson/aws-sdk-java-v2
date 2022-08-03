package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;
import software.amazon.awssdk.services.kms.model.KeyListEntry;
import software.amazon.awssdk.services.kms.model.ListKeysRequest;
import software.amazon.awssdk.services.kms.model.ListKeysResponse;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
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

public class AwsSdk2Batch {

	private BatchClient client;
	
	public AwsSdk2Batch(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = BatchClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void jobList(String jobQueue) {

    	System.out.printf("List Job ... %n");
    	
    	ListJobsRequest request = ListJobsRequest.builder()
    			.jobQueue(jobQueue)
                .build();

    	ListJobsResponse response = client.listJobs(request);
        List<JobSummary> elements = response.jobSummaryList();
        for (JobSummary element : elements) {
            System.out.println(String.format("Arn=%s Name=%s Def=%s ID=%s", 
            		element.jobArn(), element.jobName(), element.jobDefinition(), element.jobId()));
        }
        
	}

	
}
