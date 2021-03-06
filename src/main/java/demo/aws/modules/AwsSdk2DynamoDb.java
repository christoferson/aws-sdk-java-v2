package demo.aws.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import demo.aws.modules.dynamo.DynamoAttribute;
import demo.aws.modules.dynamo.DynamoAttributeList;
import demo.aws.modules.dynamo.DynamoQueryOptions;
import demo.aws.modules.dynamo.DynamoTableKey;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.dynamodb.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputDescription;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ReturnConsumedCapacity;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
//TODO:
//1 Filter Expression OK
//2 Project Expression OK
//3 PartiQL OK
//4 Auto Scaling
//5 Paging
//6 Query Index
//7 Delete Table
//8 Register - Conditional
//9 Create Table
//BatchWriteItem
//BatchGetItem
//TTL
//Data Types
//TransacGetItems
//TransacWriteItems
//ItemUpdate
public class AwsSdk2DynamoDb {

	private DynamoDbClient client;

	public AwsSdk2DynamoDb(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = DynamoDbClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();

	}

	
	public void tableList() throws DynamoDbException {

        String lastName = null;

		do {

			ListTablesRequest request = ListTablesRequest.builder()
					.exclusiveStartTableName(lastName)
					.limit(10)
					.build();
			ListTablesResponse response = client.listTables(request);

			List<String> tableNames = response.tableNames();

			for (String curName : tableNames) {
				System.out.format("* %s\n", curName);
			}

			lastName = response.lastEvaluatedTableName();

		} while (lastName != null);
			
        System.out.println("\nDone!");
    }
 
	

	public void tableDescribe(String tableName) throws DynamoDbException {

		DescribeTableRequest request = DescribeTableRequest.builder().tableName(tableName).build();
		
		TableDescription tableInfo = client.describeTable(request).table();
		if (tableInfo == null) {
			return;
		}

		System.out.format("Table Name  : %s\n", tableInfo.tableName());
		System.out.format("Table ARN   : %s\n", tableInfo.tableArn());
		System.out.format("Status      : %s\n", tableInfo.tableStatus());
		System.out.format("Item count  : %d\n", tableInfo.itemCount().longValue());
		System.out.format("Size (bytes): %d\n", tableInfo.tableSizeBytes().longValue());

		ProvisionedThroughputDescription throughputInfo = tableInfo.provisionedThroughput();
		System.out.println("Throughput");
		System.out.format("  Read Capacity  : %d\n", throughputInfo.readCapacityUnits().longValue());
		System.out.format("  Write Capacity : %d\n", throughputInfo.writeCapacityUnits().longValue());

		List<AttributeDefinition> attributes = tableInfo.attributeDefinitions();
		System.out.println("Attributes");

		for (AttributeDefinition a : attributes) {
			System.out.format("  %s (%s)\n", a.attributeName(), a.attributeType());
		}

	}
	
    public void tableScan(String tableName, DynamoQueryOptions options) throws DynamoDbException {
    	
    	Objects.requireNonNull(tableName);
    	
    	ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .limit(options.getLimit())
                    .projectionExpression(options.getProjection())
                    .filterExpression(options.getFilter())
                    .expressionAttributeValues(options.getFilterExpressionAttributeValues())
                    .consistentRead(options.getConsistentRead())
                    .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                    .build();

        ScanResponse response = client.scan(scanRequest);
        System.out.printf("Scan.Result.Count: %s %n", response.count());
        
        System.out.printf("Scan.Result.consumedCapacity.capacityUnits: %s %n", response.consumedCapacity().capacityUnits());
        for (Map<String, AttributeValue> item : response.items()) {
            System.out.println(item);
        }
        
        System.out.println("");

    }
    
    public void dynamoExecuteStatement(String statement) throws DynamoDbException {
    	
    	System.out.printf("Executing %s... %n", statement);
    	 
    	ExecuteStatementRequest scanRequest = ExecuteStatementRequest.builder()
                    .statement(statement)
                    .build();

    	ExecuteStatementResponse response = client.executeStatement(scanRequest);
    	System.out.printf("PartiQL.Result.Count: %s %n", response.items().size());
        for (Map<String, AttributeValue> item : response.items()) {
            System.out.println(item);
        }
        
        System.out.printf("Next Token: %s %n", response.nextToken());

        System.out.println("");
    }    
    
    public void itemQuery(String tableName, String regionKey) throws DynamoDbException {

        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNames = new HashMap<String,String>();
        attrNames.put("#key_region", "Region");

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues = new HashMap<String,AttributeValue>();
        attrValues.put(":"+"v_region", AttributeValue.builder().s(regionKey).build());
        
        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#key_region = :v_region")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValues)
                .limit(100) 
                .build();

        QueryResponse response = client.query(queryReq);
        System.out.println(String.format("Query.Result.Count: %s", response.count()));
        List<Map<String, AttributeValue>> items = response.items();
        for (Map<String, AttributeValue> item : items) {
        	System.out.println(String.format("%s", item));
        }
		
    }
    
    public void itemQuery(String tableName, String regionKey, DynamoQueryOptions options) throws DynamoDbException {

        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNames = new HashMap<String,String>();
        attrNames.put("#key_region", "Region");

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues = new HashMap<String,AttributeValue>();
        attrValues.put(":v_region", AttributeValue.builder().s(regionKey).build());
        attrValues.putAll(options.filterExpressionAttributeValues);
        
        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#key_region = :v_region")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValues)
                .projectionExpression(options.projection)
                .filterExpression(options.filter)
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

        QueryResponse response = client.query(queryReq);
        System.out.println(String.format("Query.Result.Count: %s", response.count()));
        System.out.printf("Query.Result.consumedCapacity.capacityUnits: %s %n", response.consumedCapacity().capacityUnits());
        List<Map<String, AttributeValue>> items = response.items();
        for (Map<String, AttributeValue> item : items) {
        	System.out.println(String.format("%s", item));
        }
		
    }
    
    public void itemQuery(String tableName, String regionKey, String sortKey) throws DynamoDbException {

        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNames = new HashMap<String,String>();
        attrNames.put("#key_region", "Region");

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues = new HashMap<String,AttributeValue>();
        attrValues.put(":"+"v_region", AttributeValue.builder().s(regionKey).build());
        attrValues.put(":"+"character_name", AttributeValue.builder().s(sortKey).build());
        
        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#key_region = :v_region and CharacterName = :character_name")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValues)
                .build();

        QueryResponse response = client.query(queryReq);
        System.out.println(String.format("Query.Result.Count: %s", response.count()));
        List<Map<String, AttributeValue>> items = response.items();
        for (Map<String, AttributeValue> item : items) {
        	System.out.println(String.format("%s", item));
        }

    }
    
    public void itemGet(DynamoTableKey tableKey) throws DynamoDbException {

    	Map<String, AttributeValue> keyMap = new HashMap<>();
        keyMap.put(tableKey.getHashKeyName(), AttributeValue.builder().s(tableKey.getHashKey()).build());
        keyMap.put(tableKey.getSortKeyName(), AttributeValue.builder().s(tableKey.getSortKey()).build());
        
        GetItemRequest queryReq = GetItemRequest.builder()
                .tableName(tableKey.getTableName())
                .key(keyMap)
                .consistentRead(false)
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

        System.out.println(String.format("Query.Key: %s", tableKey));
        GetItemResponse response = client.getItem(queryReq);
        System.out.println(String.format("Query.ConsumedCapacity: %s", response.consumedCapacity()));
        System.out.println(String.format("Query.Result: %s", response));
        Map<String, AttributeValue> item = response.item();
        System.out.println(String.format("%s", item));

    }
    
    public void itemBatchGet(List<DynamoTableKey> tableKeyList) throws DynamoDbException {

    	Map<String, KeysAndAttributes> batchKeyMap = new HashMap<>();
    	
    	for (DynamoTableKey tableKey : tableKeyList) {
	    	Map<String, AttributeValue> keyMap = new HashMap<>();
	        keyMap.put(tableKey.getHashKeyName(), AttributeValue.builder().s(tableKey.getHashKey()).build());
	        keyMap.put(tableKey.getSortKeyName(), AttributeValue.builder().s(tableKey.getSortKey()).build());	        
	        batchKeyMap.put(tableKey.getTableName(), KeysAndAttributes.builder().keys(keyMap).build());
    	}
    	
        BatchGetItemRequest request = BatchGetItemRequest.builder()
                .requestItems(batchKeyMap)
                .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                .build();

        BatchGetItemResponse response = client.batchGetItem(request);
        
        Map<String, List<Map<String, AttributeValue>>> responseMap = response.responses();
        System.out.println(String.format("Query.ConsumedCapacity: %s", response.consumedCapacity()));
        System.out.println(String.format("Query.Result.Count: %s", responseMap.size()));
        
        for (List<Map<String, AttributeValue>> batchGetResponse : responseMap.values()) {
        	for (Map<String, AttributeValue> getResponse : batchGetResponse) {
        		System.out.println(String.format("%s", getResponse));
        	}
        }

    }

	public Map<String, AttributeValue> itemRetrieve(String tableName, String partitionKey, String sortKey) throws DynamoDbException {
 
        HashMap<String,AttributeValue> itemKey = new HashMap<>();
        itemKey.put("Region", AttributeValue.builder().s(partitionKey).build());
        itemKey.put("CharacterName", AttributeValue.builder().s(sortKey).build());

        GetItemRequest request = GetItemRequest.builder()
                .key(itemKey)
                .tableName(tableName)
                .build();

        Map<String, AttributeValue> item = client.getItem(request).item();
        if (item == null) {
        	System.out.format("No item found with the key %s!\n", itemKey);
        	return null;
        }

        Set<String> keys = item.keySet();
        System.out.println(String.format("Retrieved %s.", itemKey));
        for (String key1 : keys) {
            System.out.format("%s: %s\n", key1, item.get(key1).toString());
        }
        System.out.println("----------");

        return item;

    }
	
	// ConditionalCheckFailedException - When duplicate
    public void itemRegister(String tableName, String partitionKey, String sortKey, String race, String profession) throws ResourceNotFoundException, DynamoDbException {

		HashMap<String, AttributeValue> itemValues = new HashMap<>();

		itemValues.put("Region", AttributeValue.builder().s(partitionKey).build());
		itemValues.put("CharacterName", AttributeValue.builder().s(sortKey).build());
		itemValues.put("Profession", AttributeValue.builder().s(profession).build());
		itemValues.put("Race", AttributeValue.builder().s(race).build());
		itemValues.put("Version", AttributeValue.builder().s("1").build());

		PutItemRequest request = PutItemRequest.builder()
				.tableName(tableName)
				.item(itemValues)
				.conditionExpression("attribute_not_exists(CharacterName)")
				.build();

		PutItemResponse response = client.putItem(request);
		System.out.println("" + response);

    }
    

    public void itemRegister(DynamoTableKey tableKey, DynamoAttributeList attributeList) throws ResourceNotFoundException, DynamoDbException {

		HashMap<String, AttributeValue> itemValues = new HashMap<>();

		itemValues.put(tableKey.getHashKeyName(), AttributeValue.builder().s(tableKey.getHashKey()).build());
		itemValues.put(tableKey.getSortKeyName(), AttributeValue.builder().s(tableKey.getSortKey()).build());
		itemValues.put("Version", AttributeValue.builder().s("1").build());
		for (DynamoAttribute attribute : attributeList.items()) {
			itemValues.put(attribute.name(), attribute.value());
		}

		PutItemRequest request = PutItemRequest.builder()
				.tableName(tableKey.getTableName())
				.item(itemValues)
				.conditionExpression(String.format("attribute_not_exists(%s)", tableKey.getSortKey()))
				.build();

		PutItemResponse response = client.putItem(request);
		System.out.println("" + response);

    }
    
    public void itemEditV1(String tableName, String partitionKey, String sortKey, String race, String profession, String version) 
    		throws ResourceNotFoundException, DynamoDbException {

		HashMap<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put("Region", AttributeValue.builder().s(partitionKey).build());
		itemKey.put("CharacterName", AttributeValue.builder().s(sortKey).build());

		String newVersion = String.valueOf(Long.valueOf(version) + 1);
		HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<String, AttributeValueUpdate>();
		updatedValues.put("Version", AttributeValueUpdate.builder().value(AttributeValue.builder().s(newVersion).build()).action(AttributeAction.PUT).build());
		updatedValues.put("Race", AttributeValueUpdate.builder().value(AttributeValue.builder().s(race).build()).action(AttributeAction.PUT).build());
		updatedValues.put("Profession", AttributeValueUpdate.builder().value(AttributeValue.builder().s(profession).build()).action(AttributeAction.PUT).build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        client.updateItem(request);
        System.out.println("Done!");

    }
    
    public void itemEdit(String tableName, String partitionKey, String sortKey, String race, String profession, String version) 
    		throws ResourceNotFoundException, DynamoDbException {

		HashMap<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put("Region", AttributeValue.builder().s(partitionKey).build());
		itemKey.put("CharacterName", AttributeValue.builder().s(sortKey).build());

		String newVersion = String.valueOf(Long.valueOf(version) + 1);

		HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();
		itemValues.put(":v_profession", AttributeValue.builder().s(profession).build());
		itemValues.put(":v_race", AttributeValue.builder().s(race).build());
		itemValues.put(":v_version", AttributeValue.builder().s(newVersion).build());
		itemValues.put(":v_current_version", AttributeValue.builder().s(version).build());
		
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .updateExpression("SET Version = :v_version, Race = :v_race, Profession = :v_profession")
                .expressionAttributeValues(itemValues)
                .conditionExpression("Version = :v_current_version") // Optimistic Locking
                .build();

        client.updateItem(request);
        System.out.println("Done!");

    }    
    
    public void itemEdit(DynamoTableKey tableKey, DynamoAttributeList attributeList, String version) 
    		throws ResourceNotFoundException, DynamoDbException {

    	System.out.println(tableKey);
    	
		HashMap<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put(tableKey.getHashKeyName(), AttributeValue.builder().s(tableKey.getHashKey()).build());
		itemKey.put(tableKey.getSortKeyName(), AttributeValue.builder().s(tableKey.getSortKey()).build());

		String newVersion = String.valueOf(Long.valueOf(version) + 1);

		HashMap<String, AttributeValue> itemValues = new HashMap<>();
		//itemValues.put(":v_profession", AttributeValue.builder().s(profession).build());
		//itemValues.put(":v_race", AttributeValue.builder().s(race).build());
		itemValues.put(":v_version", AttributeValue.builder().s(newVersion).build());
		itemValues.put(":v_current_version", AttributeValue.builder().s(version).build());
		
		StringBuilder updateExpression = new StringBuilder("SET Version = :v_version");
		for (DynamoAttribute attribute : attributeList.items()) {
			String placeholder = String.format(":v_%s", attribute.name().toLowerCase());
			itemValues.put(placeholder, attribute.value());
			updateExpression.append(String.format(", %s = %s", attribute.name(), placeholder));
		}
		System.out.println(updateExpression.toString());
		System.out.println(itemValues);
		
        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableKey.getTableName())
                .key(itemKey)
                .updateExpression(updateExpression.toString())
                .expressionAttributeValues(itemValues)
                // software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
                .conditionExpression("Version = :v_current_version") // Optimistic Locking
                .build();

        client.updateItem(request);
        System.out.println("Done!");

    }        
    
    public void itemDelete(String tableName, String partitionKey, String sortKey) throws DynamoDbException {

		HashMap<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put("Region", AttributeValue.builder().s(partitionKey).build());
		itemKey.put("CharacterName", AttributeValue.builder().s(sortKey).build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .conditionExpression("attribute_exists(CharacterName)")
                .build();

        client.deleteItem(deleteReq);
        
        System.out.println(String.format("Deleted %s", itemKey));

    }
    
    public void itemDelete(DynamoTableKey tableKey) throws DynamoDbException {

		HashMap<String, AttributeValue> itemKey = new HashMap<>();
		itemKey.put(tableKey.getHashKeyName(), AttributeValue.builder().s(tableKey.getHashKey()).build());
		itemKey.put(tableKey.getSortKeyName(), AttributeValue.builder().s(tableKey.getSortKey()).build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(tableKey.getTableName())
                .key(itemKey)
                .conditionExpression(String.format("attribute_exists(%s)", tableKey.getSortKeyName()))
                .build();

        DeleteItemResponse response = client.deleteItem(deleteReq);
        
        System.out.println(String.format("Deleted %s. Response~%s", itemKey, response));

    }
    
    
/*	

    
    // What if attribute already exists // How to remove attribute
    public void itemUpdateAddNewAttribute(String tableName, String keyRegionId, String keyPlayerId) {

        Table table = dynamoDB.getTable(tableName);

        try {

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
            	.withPrimaryKey("Region", keyRegionId, "PlayerID", keyPlayerId)
                .withUpdateExpression("set #na = :val1")
                .withNameMap(new NameMap().with("#na", "MaxHitPoints"))
                .withValueMap(new ValueMap().withString(":val1", "150"))
                .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response.
            System.out.println("Printing item after adding new attribute...");
            System.out.println(outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Failed to add new attribute in " + tableName);
            System.err.println(e.getMessage());
        }

    }
    
    public void executePartiQL(String partiQLString) {

        ExecuteStatementRequest request = new ExecuteStatementRequest().withStatement(partiQLString);
        ExecuteStatementResult result = client.executeStatement(request);
        List<Map<String, AttributeValue>> items = result.getItems();
		int count = 0;
		for (var item : items) {
			System.out.println(item);
			count++;
		}
		if (count == 0) {
			System.out.println("No Matches");
		}
    }
    

    
    public void describeDymamoDBTable(String tableName) {

		DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);

		TableDescription tableInfo = client.describeTable(request).getTable();

		if (tableInfo != null) {
			System.out.format("Table name  : %s\n", tableInfo.getTableName());
			System.out.format("Table ARN   : %s\n", tableInfo.getTableArn());
			System.out.format("Status      : %s\n", tableInfo.getTableStatus());
			System.out.format("Item count  : %d\n", tableInfo.getItemCount().longValue());
			System.out.format("Size (bytes): %d\n", tableInfo.getTableSizeBytes().longValue());

			ProvisionedThroughputDescription throughputInfo = tableInfo.getProvisionedThroughput();
			System.out.println("Throughput");
			System.out.format("  Read Capacity : %d\n", throughputInfo.getReadCapacityUnits().longValue());
			System.out.format("  Write Capacity: %d\n", throughputInfo.getWriteCapacityUnits().longValue());

			List<AttributeDefinition> attributes = tableInfo.getAttributeDefinitions();
			System.out.println("Attributes");

			for (AttributeDefinition a : attributes) {
				System.out.format("  %s (%s)\n", a.getAttributeName(), a.getAttributeType());
			}
		}

        System.out.println("\nDone!");
    }
    */

}
