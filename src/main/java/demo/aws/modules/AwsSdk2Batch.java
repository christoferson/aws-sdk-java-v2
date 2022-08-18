package demo.aws.modules;

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
import software.amazon.awssdk.services.batch.model.JobTimeout;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;
import software.amazon.awssdk.services.batch.model.SubmitJobResponse;


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

	public void computeEnvironmentDescribe() {

    	System.out.printf("Describe ComputeEnvironment ... %n");
    	
    	DescribeComputeEnvironmentsRequest request = DescribeComputeEnvironmentsRequest.builder()
                .build();

    	DescribeComputeEnvironmentsResponse response = client.describeComputeEnvironments(request);
        List<ComputeEnvironmentDetail> elements = response.computeEnvironments();
        for (ComputeEnvironmentDetail element : elements) {
            System.out.println(String.format("Arn=%s Name=%s", 
            		element.computeEnvironmentArn(), element.computeEnvironmentName()));
        }
        
	}
	
	public void jobQueueDescribe() {

    	System.out.printf("Describe JobQueue ... %n");
    	
    	DescribeJobQueuesRequest request = DescribeJobQueuesRequest.builder()
                .build();

    	DescribeJobQueuesResponse response = client.describeJobQueues(request);
        List<JobQueueDetail> elements = response.jobQueues();
        for (JobQueueDetail element : elements) {
            System.out.println(String.format("Arn=%s Name=%s", 
            		element.jobQueueArn(), element.jobQueueName()));
        }
        
	}
	
	public void jobDefinitionDescribe() {

    	System.out.printf("Describe JobDefinition ... %n");
    	
    	DescribeJobDefinitionsRequest request = DescribeJobDefinitionsRequest.builder()
                .build();

    	DescribeJobDefinitionsResponse response = client.describeJobDefinitions(request);
        List<JobDefinition> elements = response.jobDefinitions();
        for (JobDefinition element : elements) {
            System.out.println(String.format("Arn=%s Name=%s", 
            		element.jobDefinitionArn(), element.jobDefinitionName()));
        }
        
	}
	
	public void jobSubmit(String jobDefinition, String jobQueue, String jobName) {

    	System.out.printf("Sumit Job Def=%s Queue=%s Name=%s... %n", jobDefinition, jobQueue, jobName);
    	
    	SubmitJobRequest request = SubmitJobRequest.builder()
    			.jobDefinition(jobDefinition)
    			.jobQueue(jobQueue)
    			.jobName(jobName)
                .build();

    	SubmitJobResponse response = client.submitJob(request);
    	SubmitJobResponse element = response;
        System.out.println(String.format("Arn=%s Name=%s ID=%s", 
        		element.jobArn(), element.jobName(), element.jobId()));
        
	}
	
}
