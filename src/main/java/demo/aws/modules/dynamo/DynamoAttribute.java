package demo.aws.modules.dynamo;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoAttribute {
	
	private String name;
	private AttributeValue value;
	
	public static DynamoAttribute builder(String name) {
		DynamoAttribute attribute = new DynamoAttribute();
		attribute.name = name;
		return attribute;
	}

	public static DynamoAttribute builder(String name, String value) {
		DynamoAttribute attribute = new DynamoAttribute();
		attribute.name = name;
		attribute.value = AttributeValue.builder().s(value).build();
		return attribute;
	}
	
	public String name() {
		return this.name;
	}
	
	public AttributeValue value() {
		return this.value;
	}
	
	public DynamoAttribute withValue(String value) {
		this.value = AttributeValue.builder().s(value).build();
		return this;
	}

}
