package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;

public class AwsSdk2Lambda {

	private LambdaClient client;
	
	public AwsSdk2Lambda(AwsCredentials credentials, Region region) {
		
		this.client = LambdaClient.builder()
				  .credentialsProvider(StaticCredentialsProvider.create(credentials))
				  .region(region)
				  .build();
	}
	
	public void list() {

        try {
            ListFunctionsResponse functionResult = client.listFunctions();
            List<FunctionConfiguration> list = functionResult.functions();

            for (FunctionConfiguration config: list) {
                System.out.println("The function name is "+config.functionName());
            }

        } catch(LambdaException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
	}
	
	
}
