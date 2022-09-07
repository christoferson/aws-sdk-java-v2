package demo.aws.modules;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.DashboardEntry;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.DimensionFilter;
import software.amazon.awssdk.services.cloudwatch.model.ListDashboardsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListDashboardsResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.Metric;

public class AwsSdk2Cloudwatch {

	private CloudWatchClient  client;
	
	public AwsSdk2Cloudwatch(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CloudWatchClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void metricsList(String namespace, String metricName) {
		
		System.out.println(String.format("List CloudWatch Metrics"));

    	ListMetricsRequest request = ListMetricsRequest.builder()
    			.namespace(namespace)
    			.metricName(metricName)
    			//AWS/S3 NumberOfObjects .dimensions(DimensionFilter.builder().name("BucketName").value("demo-cloudformation").build())
    			.build();
    	ListMetricsResponse result = client.listMetrics(request);
        List<Metric> list = result.metrics();

        for (Metric element : list) {
            System.out.println(String.format("%s %s", element.namespace(), element.metricName()));
            for (Dimension dimension : element.dimensions()) {
            	System.out.println(String.format("  %s %s", dimension.name(), dimension.value()));
            }
        }
        
	}

	public void dashboardList() {
		
		System.out.println(String.format("List CloudWatch Dashboard"));

		ListDashboardsRequest request = ListDashboardsRequest.builder()
    			//.metricName("ConcurrentExecutions")
    			.build();
		ListDashboardsResponse result = client.listDashboards(request);
        List<DashboardEntry> list = result.dashboardEntries();

        for (DashboardEntry element : list) {
            System.out.println(String.format("%s %s", element.dashboardName(), element.dashboardArn()));
        }
        
	}
	
}
