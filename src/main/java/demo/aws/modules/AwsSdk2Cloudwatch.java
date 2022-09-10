package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.DashboardEntry;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.DimensionFilter;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricStatisticsResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListDashboardsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListDashboardsResponse;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsRequest;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;
import software.amazon.awssdk.services.cloudwatch.model.MessageData;
import software.amazon.awssdk.services.cloudwatch.model.Metric;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataQuery;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatch.model.Statistic;

public class AwsSdk2Cloudwatch {

	private CloudWatchClient  client;
	
	public AwsSdk2Cloudwatch(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CloudWatchClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void metricsList(String namespace, String metricName, DimensionFilter ... dimensions) {
		
		System.out.println(String.format("List CloudWatch Metrics"));

    	ListMetricsRequest request = ListMetricsRequest.builder()
    			.namespace(namespace)
    			.metricName(metricName)
    			.dimensions(dimensions)
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
	
	public void metricDataGet(String namespace, String metricName) {
		
		System.out.println(String.format("List CloudWatch Metrics"));

		GetMetricDataRequest request = GetMetricDataRequest.builder()
				.startTime(Instant.now())
				.endTime(Instant.now().plus(3, ChronoUnit.DAYS))
				.metricDataQueries(MetricDataQuery.builder().build()) //TODO:
    			.build();
		GetMetricDataResponse result = client.getMetricData(request);
        List<MetricDataResult> list = result.metricDataResults();

        for (MetricDataResult element : list) {
            System.out.println(String.format("%s %s", element.id(), element.label()));
            for (MessageData message : element.messages()) {
            	System.out.println(String.format("  %s %s", message.code(), message.value()));
            }
        }
        
	}
	
	public void metricStatisticGet(String namespace, String metricName, Dimension ... dimensions) {
		
		System.out.println(String.format("List CloudWatch MetricStatistic"));

		GetMetricStatisticsRequest request = GetMetricStatisticsRequest.builder()
				.startTime(Instant.now().minus(5, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS))
				.endTime(Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS))
				.namespace(namespace)
				.metricName(metricName)
				.statistics(Statistic.SUM)
				.period(3600)
				.dimensions(dimensions)
    			.build();
		GetMetricStatisticsResponse result = client.getMetricStatistics(request);
        List<Datapoint> list = result.datapoints();

        for (Datapoint element : list) {
            System.out.println(String.format("%s %s %s", element.timestamp(), element.unit(), element.sum()));
        }
        
	}
	
	
	public void metricPut(String namespace, String metricName, Double metricValue, Map<String, String> dimensionMap) {
		
		System.out.println(String.format("Put CloudWatch Metric. Namespace=%s Name=%s Dimension=%s", namespace, metricName, dimensionMap));

		List<Dimension> dimensions = dimensionMap.entrySet().stream()
			.map(entry -> Dimension.builder().name(entry.getKey()).value(entry.getValue()).build())
			.collect(Collectors.toList());
		
		PutMetricDataRequest request = PutMetricDataRequest.builder()
				.namespace(namespace)
    			.metricData(MetricDatum.builder()
    					.metricName(metricName)
    					.unit(StandardUnit.COUNT)
    					.value(metricValue)
    					.dimensions(dimensions)
    					.timestamp(Instant.now().truncatedTo(ChronoUnit.MILLIS))
    					.build())
    			.build();

		PutMetricDataResponse result = client.putMetricData(request);
		System.out.println(result);
        
	}
	
	public void dashboardList() {
		
		System.out.println(String.format("List CloudWatch Dashboard"));

		ListDashboardsRequest request = ListDashboardsRequest.builder()
    			.build();
		
		ListDashboardsResponse result = client.listDashboards(request);
        List<DashboardEntry> list = result.dashboardEntries();

        for (DashboardEntry element : list) {
            System.out.println(String.format("%s %s", element.dashboardName(), element.dashboardArn()));
        }
        
	}
	
}
