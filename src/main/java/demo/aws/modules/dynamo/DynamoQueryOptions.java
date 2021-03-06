package demo.aws.modules.dynamo;

import java.util.HashMap;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoQueryOptions {
	public String projection;
	public String filter;
	public Integer limit;
	public Boolean consistentRead;
	public HashMap<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
	
	public static DynamoQueryOptions get() {
		return new DynamoQueryOptions();
	}

	// Mutators
	
	public DynamoQueryOptions withProjection(String projection) {
		this.projection = projection;
		return this;
	}
	public DynamoQueryOptions withFilter(String filter, DynamoAttribute ... values) {
		this.filter = filter;
		for (DynamoAttribute value : values) {
			this.filterExpressionAttributeValues.put(value.name(), value.value());
		}
		return this;
	}
	public DynamoQueryOptions withLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	public DynamoQueryOptions withConsistentRead(Boolean consistentRead) {
		this.consistentRead = consistentRead;
		return this;
	}
	
	// Getters
	
	public String getProjection() {
		return projection;
	}
	public String getFilter() {
		return filter;
	}
	public Integer getLimit() {
		return limit;
	}
	public Boolean getConsistentRead() {
		return consistentRead;
	}
	public HashMap<String, AttributeValue> getFilterExpressionAttributeValues() {
		return filterExpressionAttributeValues;
	}	
	
}
