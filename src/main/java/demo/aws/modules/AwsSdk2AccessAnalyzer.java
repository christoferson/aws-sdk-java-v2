package demo.aws.modules;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.accessanalyzer.AccessAnalyzerClient;
import software.amazon.awssdk.services.accessanalyzer.model.AnalyzerSummary;
import software.amazon.awssdk.services.accessanalyzer.model.DeleteAnalyzerRequest;
import software.amazon.awssdk.services.accessanalyzer.model.DeleteAnalyzerResponse;
import software.amazon.awssdk.services.accessanalyzer.model.ListAnalyzersRequest;
import software.amazon.awssdk.services.accessanalyzer.model.ListAnalyzersResponse;

public class AwsSdk2AccessAnalyzer {

	private AccessAnalyzerClient client;
	
	public AwsSdk2AccessAnalyzer(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = AccessAnalyzerClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void analyzerList() {
		
		System.out.println(String.format("List Access Analyzers"));

		ListAnalyzersRequest request = ListAnalyzersRequest.builder()
    			.build();
		
		ListAnalyzersResponse result = client.listAnalyzers(request);
        List<AnalyzerSummary> list = result.analyzers();

        for (var element : list) {
            System.out.println(String.format("%s Analyze=%s", element.name(), element.lastResourceAnalyzedAt()));
        }
        
	}
	
	public void analyzerDelete(String name) {
		
		System.out.println(String.format("Delete Access Analyzer. Name=%s", name));

		DeleteAnalyzerRequest request = DeleteAnalyzerRequest.builder()
				.analyzerName(name)
    			.build();
		
		DeleteAnalyzerResponse result = client.deleteAnalyzer(request);
        
        System.out.println(String.format("%s", result));
        
        
	}
	
}
