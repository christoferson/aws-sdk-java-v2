package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;

public class AwsSdk2Lambda {

	private LambdaClient client;
	
	public AwsSdk2Lambda(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = LambdaClient.builder()
				  .credentialsProvider(credentialsProvider)
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
	
    public void invokeFunction(String functionName) {

       InvokeResponse res = null ;
       try {
  
           //Need a SdkBytes instance for the payload
           String json = "{\"Hello \":\"Paris\"}";
           SdkBytes payload = SdkBytes.fromUtf8String(json) ;

           //Setup an InvokeRequest
           InvokeRequest request = InvokeRequest.builder()
                   .functionName(functionName)
                   .payload(payload)
                   .build();

           res = client.invoke(request);
           String value = res.payload().asUtf8String() ;
           System.out.println(value);

       } catch(LambdaException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
   }
	
	
}
