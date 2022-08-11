package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.ComputeEnvironmentDetail;
import software.amazon.awssdk.services.batch.model.DescribeComputeEnvironmentsRequest;
import software.amazon.awssdk.services.batch.model.DescribeComputeEnvironmentsResponse;
import software.amazon.awssdk.services.batch.model.DescribeJobDefinitionsRequest;
import software.amazon.awssdk.services.batch.model.DescribeJobDefinitionsResponse;
import software.amazon.awssdk.services.batch.model.DescribeJobQueuesRequest;
import software.amazon.awssdk.services.batch.model.DescribeJobQueuesResponse;
import software.amazon.awssdk.services.batch.model.JobDefinition;
import software.amazon.awssdk.services.batch.model.JobQueueDetail;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;


public class AwsSdk2Batch {

	private BatchClient client;
	
	public AwsSdk2Batch(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = BatchClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void jobList(String jobQueue, JobStatus jobStatus) {

    	System.out.printf("List Job Queue=%s State=%s... %n", jobQueue, jobStatus);
    	
    	ListJobsRequest request = ListJobsRequest.builder()
    			.jobQueue(jobQueue).jobStatus(jobStatus)
                .build();

    	ListJobsResponse response = client.listJobs(request);
        List<JobSummary> elements = response.jobSummaryList();
        for (JobSummary element : elements) {
            System.out.println(String.format("Arn=%s Name=%s Def=%s ID=%s", 
            		element.jobArn(), element.jobName(), element.jobDefinition(), element.jobId()));
        }
        
	}

	
}
