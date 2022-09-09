package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.DashboardEntry;
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
import software.amazon.awssdk.services.cloudwatch.model.Statistic;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogStreamsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilteredLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.GetLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogStream;
import software.amazon.awssdk.services.cloudwatchlogs.model.MetricTransformation;
import software.amazon.awssdk.services.cloudwatchlogs.model.OutputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutMetricFilterRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutMetricFilterResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.StandardUnit;

//TODO:
// List & Create Log Groups
// Log Streams
// Metric Filters - Create Alaram
// Subscription Filters - Lambda
// Contributor Insights
// Insights
//fields @timestamp, @message
//| sort @timestamp desc
//| limit 20
public class AwsSdk2CloudwatchLogs {

	private CloudWatchLogsClient  client;
	
	public AwsSdk2CloudwatchLogs(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = CloudWatchLogsClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void logGroupList(String prefix) {
		
		System.out.println(String.format("List CloudWatch LogGroups"));

		DescribeLogGroupsRequest request = DescribeLogGroupsRequest.builder()
				.logGroupNamePrefix(prefix)
    			.build();
		
		DescribeLogGroupsResponse result = client.describeLogGroups(request);
        List<LogGroup> list = result.logGroups();

        for (LogGroup element : list) {
            System.out.println(String.format("%s %s %s", element.logGroupName(), element.retentionInDays(), element.metricFilterCount()));
        }
        
	}

	public void logStreamList(String logGroupName, String prefix) {
		
		System.out.println(String.format("List CloudWatch LogStream"));

		DescribeLogStreamsRequest request = DescribeLogStreamsRequest.builder()
				.logGroupName(logGroupName)
				.logStreamNamePrefix(prefix)
    			.build();
		
		DescribeLogStreamsResponse result = client.describeLogStreams(request);
        List<LogStream> list = result.logStreams();

        for (LogStream element : list) {
            System.out.println(String.format("%s %s %s", element.logStreamName(), element.firstEventTimestamp(), element.lastEventTimestamp()));
        }
        
	}

	public void logEventList(String logGroupName, String logStreamName, Long startTime, Long endTime) {
		
		System.out.println(String.format("List CloudWatch LogEvent"));

		GetLogEventsRequest request = GetLogEventsRequest.builder()
				.logGroupName(logGroupName)
				.logStreamName(logStreamName)
				.startTime(startTime)
				.endTime(endTime)
    			.build();
		
		GetLogEventsResponse result = client.getLogEvents(request);
        List<OutputLogEvent> list = result.events();

        for (OutputLogEvent element : list) {
            System.out.println(String.format("%s %s", element.timestamp(), element.message()));
        }
        
	}
	
	public void logEventFilter(String logGroupName, String logStreamName) {
		
		System.out.println(String.format("Filter CloudWatch LogEvent"));

		FilterLogEventsRequest request = FilterLogEventsRequest.builder()
				.logGroupName(logGroupName)
				.logStreamNames(logStreamName)
				.filterPattern("ERROR")
    			.build();
		
		FilterLogEventsResponse result = client.filterLogEvents(request);
        List<FilteredLogEvent> list = result.events();

        for (FilteredLogEvent element : list) {
            System.out.println(String.format("%s %s", element.timestamp(), element.message()));
        }
        
	}
	
	//

	// Valid metric values are: floating point number (1, 99.9, etc.), numeric field identifiers ($1, $2, etc.), 
	// or named field identifiers (e.g. $requestSize for delimited filter pattern or 
	// $.status for JSON-based filter pattern - dollar ($) or dollar dot ($.) followed by alphanumeric and/or underscore (_) characters)
	public void metricFilterRegister(String logGroupName, String filterName, String filterPattern) {
		
		System.out.println(String.format("Register CloudWatch MetricFilter"));

		PutMetricFilterRequest request = PutMetricFilterRequest.builder()
				.filterName(filterName)
				.filterPattern(filterPattern)
				.logGroupName(logGroupName)
				.metricTransformations(MetricTransformation.builder()
						.metricNamespace("MyNamespace")
						.metricName("FooErrorCount")
						.unit(StandardUnit.COUNT) //Optional
						.metricValue("1") 
						.defaultValue(0.0)
						//.dimensions(new HashMap<String, String>() {{ put("X", "037434729"); put("Y", "81739838"); }})
						.build())
    			.build();
		
		PutMetricFilterResponse result = client.putMetricFilter(request);
        System.out.println(result);
        
	}
}
