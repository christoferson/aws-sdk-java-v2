package demo.aws.modules.dynamo;

public class DynamoTableKey {

	private String tableName; 
	private String hashKeyName; 
	private String hashKey; 
	private String sortKeyName;
	private String sortKey;

	public DynamoTableKey(String tableName) {
		this.tableName = tableName;
	}
	
	public DynamoTableKey withHashKey(String hashKeyName, String hashKey) {
		this.hashKeyName = hashKeyName;
		this.hashKey = hashKey;
		return this;
	}

	public DynamoTableKey withSortKey(String sortKeyName, String sortKey) {
		this.sortKeyName = sortKeyName;
		this.sortKey = sortKey;
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public String getHashKeyName() {
		return hashKeyName;
	}

	public String getHashKey() {
		return hashKey;
	}

	public String getSortKeyName() {
		return sortKeyName;
	}

	public String getSortKey() {
		return sortKey;
	}

	@Override
	public String toString() {
		return String.format("DynamoTableKey [tableName=%s, hashKeyName=%s, hashKey=%s, sortKeyName=%s, sortKey=%s]",
				tableName, hashKeyName, hashKey, sortKeyName, sortKey);
	}
	
	
	
}
