package demo.aws.modules;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Random;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

public class AwsSdk2S3 {

	private S3Client  client;
	
	public AwsSdk2S3(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = S3Client.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .overrideConfiguration(builder -> builder
					.apiCallTimeout(Duration.ofMinutes(2))
					//.apiCallAttemptTimeout(/* 15 min */)
					//.retryPolicy(RetryMode.STANDARD)
					.build())
				  .build();
	}
	
//	@Provides @Singleton
//	S3AsyncClient providesS3AsyncClient(AwsCredentialsProvider credentials) {
//	  return S3AsyncClient.builder()
//	      .region(Region.of(config.getString("region")))
//	      .credentialsProvider(credentials)
//	      .overrideConfiguration(builder -> builder
//	        .apiCallTimeout(/* 45 min */)
//	        .apiCallAttemptTimeout(/* 15 min */)
//	      ).build();
//	}
	
    public void multipartUpload(String bucketName, String key) throws IOException {

        int mB = 1024 * 1024;

        // First create a multipart upload and get the upload id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();
        System.out.println(uploadId);

        // Upload all the different parts of the object
        UploadPartRequest uploadPartRequest1 = UploadPartRequest.builder()
                .bucket(bucketName)
                 .key(key)
                .uploadId(uploadId)
                .partNumber(1).build();

        String etag1 = client.uploadPart(uploadPartRequest1, RequestBody.fromByteBuffer(getRandomByteBuffer(5 * mB))).eTag();

        CompletedPart part1 = CompletedPart.builder().partNumber(1).eTag(etag1).build();

        UploadPartRequest uploadPartRequest2 = UploadPartRequest.builder().bucket(bucketName).key(key)
                .uploadId(uploadId)
                .partNumber(2).build();
        String etag2 = client.uploadPart(uploadPartRequest2, RequestBody.fromByteBuffer(getRandomByteBuffer(3 * mB))).eTag();
        CompletedPart part2 = CompletedPart.builder().partNumber(2).eTag(etag2).build();


        // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
        // parts and finish the multipart operation.
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(part1, part2)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        client.completeMultipartUpload(completeMultipartUploadRequest);

    }

    private static ByteBuffer getRandomByteBuffer(int size) throws IOException {
        byte[] b = new byte[size];
        new Random().nextBytes(b);
        return ByteBuffer.wrap(b);
    }
    
}
