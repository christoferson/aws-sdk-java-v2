package demo.aws.modules;

import java.nio.charset.Charset;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescription;

public class AwsSdk2Kinesis {

	private KinesisClient client;
	
	public AwsSdk2Kinesis(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = KinesisClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void describeStream(String streamName) {

    	System.out.printf("DescribeStream StreamName=%s... %n", streamName);
    	
    	DescribeStreamRequest request = DescribeStreamRequest.builder()
    			.streamName(streamName)
                .build();

    	DescribeStreamResponse response = client.describeStream(request);
    	StreamDescription description = response.streamDescription();
    	
    	System.out.printf("Stream.Name=%s %n", description.streamName());
    	System.out.printf("Stream.Status=%s %n", description.streamStatusAsString());
    	System.out.printf("Stream.CreationTimestamp=%s %n", description.streamCreationTimestamp());
        List<Shard> elements = response.streamDescription().shards();
        for (Shard element : elements) {
            System.out.println(String.format("Shard ShardID=%s ", 
            		element.shardId()));
        }
        
	}

	public void putRecord(String streamName, String partitionKey, String data) {

    	System.out.printf("DescribeStream StreamName=%s PartitionKey=%s Data=%s... %n", streamName, partitionKey, data);
    	
    	PutRecordRequest request = PutRecordRequest.builder()
    			.streamName(streamName)
    			.partitionKey(partitionKey)
    			.data(SdkBytes.fromUtf8String(data))
                .build();

    	PutRecordResponse response = client.putRecord(request);
    	
    	System.out.printf("Response.ShardId=%s %n", response.shardId());
    	System.out.printf("Response.SequenceNumber=%s %n", response.sequenceNumber());
    	System.out.printf("Response.EncryptionType=%s %n", response.encryptionType());
        
	}
	
	public void listRecords(String streamArn, String shardId) {

    	System.out.printf("ListRecords StreamName=%s... %n", streamArn);
    	
    	// Get Iterator
    	
    	GetShardIteratorRequest itrrequest = GetShardIteratorRequest.builder()
    			.streamARN(streamArn)
    			.shardId(shardId)
    			.shardIteratorType(ShardIteratorType.TRIM_HORIZON)
                .build();

    	GetShardIteratorResponse itrresponse = client.getShardIterator(itrrequest);
    	String shardIteratorId = itrresponse.shardIterator();
    	
    	System.out.printf("Stream.shardIteratorId=%s %n", shardIteratorId);
    	
    	// Get Items
    	
    	GetRecordsRequest request = GetRecordsRequest.builder()
    			.streamARN(streamArn)
    			.shardIterator(shardIteratorId)
                .build();

    	GetRecordsResponse response = client.getRecords(request);
        List<Record> elements = response.records();
        for (Record element : elements) {
            String data = element.data().asString(Charset.forName("UTF-8"));
			System.out.println(String.format("Record sequenceNumber=%s partitionKey=%s Data=%s", 
            		element.sequenceNumber(), element.partitionKey(), data));
        }

	}

	
}
