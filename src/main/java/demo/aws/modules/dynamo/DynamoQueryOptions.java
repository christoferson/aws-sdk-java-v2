package demo.aws.modules.dynamo;

public class DynamoQueryOptions {
	public String projection;
	public String filter;
	public static DynamoQueryOptions get() {
		return new DynamoQueryOptions();
	}
	public DynamoQueryOptions withProjection(String projection) {
		this.projection = projection;
		return this;
	}
	public DynamoQueryOptions withFilter(String filter) {
		this.filter = filter;
		return this;
	}
}
