package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

public class AwsSdk2Ssm {

	private SsmClient client;
	
	public AwsSdk2Ssm(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = SsmClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .httpClientBuilder(UrlConnectionHttpClient.builder())
				  .build();
	}
	

	public void parametersByPath(String path) {
		
		System.out.println(String.format("Get ParametersByPath"));
		
		GetParametersByPathRequest request = GetParametersByPathRequest.builder()
				.path(path)
				.build();

		GetParametersByPathResponse result = client.getParametersByPath(request);
	    List<Parameter> list = result.parameters();
	
	    for (Parameter element : list) {
	        System.out.println(String.format("%s %s", element.name(), element.value()));
	    }
        
	}
	
}
