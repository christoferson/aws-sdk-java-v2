package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Address;
import software.amazon.awssdk.services.ec2.model.AvailabilityZone;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAddressesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeImagesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeImagesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointServicesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVpcEndpointServicesResponse;
import software.amazon.awssdk.services.ec2.model.Image;
import software.amazon.awssdk.services.ec2.model.InstanceStatus;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.ServiceDetail;


public class AwsSdk2Ec2 {

	private Ec2Client client;
	
	public AwsSdk2Ec2(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = Ec2Client.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
	}
	
	public void imagesDescribe() {
		
		System.out.println(String.format("Describe Images"));
		
		DescribeImagesRequest request = DescribeImagesRequest.builder()
				.build();

		DescribeImagesResponse result = client.describeImages(request);
	    List<Image> list = result.images();
	
	    for (Image image : list) {
	        System.out.println(String.format("%s", image));
	    }
        
	}
	
	public void instancesDescribe() {
		
		System.out.println(String.format("Describe Instances"));
		
		DescribeInstancesRequest request = DescribeInstancesRequest.builder()
				.build();

		DescribeInstancesResponse result = client.describeInstances(request);
	    List<Reservation> list = result.reservations();
	
	    for (Reservation reservation : list) {
	    	if (reservation.hasInstances()) {
	    		System.out.println(String.format("%s", reservation));
	    	}
	    }
        
	}

	public void addressesDescribe() {
		
		System.out.println(String.format("Describe Addresses"));
		
		DescribeAddressesRequest request = DescribeAddressesRequest.builder()
				.build();

		DescribeAddressesResponse result = client.describeAddresses(request);
	    List<Address> list = result.addresses();
	
	    for (Address address : list) {
	    	System.out.println(String.format("%s", address));
	    }
        
	}	

	public void availabilityZonesDescribe() {
		
		System.out.println(String.format("Describe AvailabilityZones"));
		
		DescribeAvailabilityZonesRequest request = DescribeAvailabilityZonesRequest.builder()
				.build();

		DescribeAvailabilityZonesResponse result = client.describeAvailabilityZones(request);
	    List<AvailabilityZone> list = result.availabilityZones();
	
	    for (AvailabilityZone address : list) {
	    	System.out.println(String.format("%s", address));
	    }
        
	}	
	
	public void instanceStatusDescribe() {
		
		System.out.println(String.format("Describe InstanceStatus"));
		
		DescribeInstanceStatusRequest request = DescribeInstanceStatusRequest.builder()
				.build();

		DescribeInstanceStatusResponse result = client.describeInstanceStatus(request);
	    List<InstanceStatus> list = result.instanceStatuses();
	
	    for (InstanceStatus element : list) {
	    	System.out.println(String.format("%s", element));
	    }
        
	}	
	
	public void vpcEndpointServiceDescribe() {
		
		System.out.println(String.format("Describe Vpc Endpoint Services"));
		
		DescribeVpcEndpointServicesRequest request = DescribeVpcEndpointServicesRequest.builder()
				.build();

		DescribeVpcEndpointServicesResponse result = client.describeVpcEndpointServices(request);
	    List<ServiceDetail> list = result.serviceDetails();
	
	    for (ServiceDetail element : list) {
	    	System.out.println(String.format("%s", element));
	    }
        
	}	
	
}
