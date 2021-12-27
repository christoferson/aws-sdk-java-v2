package demo.aws.modules;

import java.util.Iterator;

import demo.aws.modules.dynamo.GameCharacter;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class AwsSdk2DynamoDbEnhanced {

	private DynamoDbEnhancedClient client;

	public AwsSdk2DynamoDbEnhanced(AwsCredentialsProvider credentialsProvider, Region region) {

		DynamoDbClient rclient = DynamoDbClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();
		
		this.client = DynamoDbEnhancedClient.builder()
				  .dynamoDbClient(rclient)
				  .build();

	}
	
	
    public void tableScan(String tableName) throws DynamoDbException {
 
        DynamoDbTable<GameCharacter> characterTable = client.table(tableName, TableSchema.fromBean(GameCharacter.class));
        Iterator<GameCharacter> results = characterTable.scan().items().iterator();
        while (results.hasNext()) {
        	GameCharacter rec = results.next();
            System.out.println(rec);
        }

    }
    
	public void itemRetrieve(String tableName, String partitionKey, String sortKey) throws DynamoDbException {
		
        BeanTableSchema<GameCharacter> schema = TableSchema.fromBean(GameCharacter.class);
        System.out.println(schema.tableMetadata().primaryPartitionKey());
        System.out.println(schema.tableMetadata().primarySortKey());

        DynamoDbTable<GameCharacter> mappedTable = client.table(tableName, schema);

        Key key = Key.builder()
                .partitionValue(partitionKey)
                .sortValue(sortKey)
                .build();

        GameCharacter result = mappedTable.getItem(key);
        System.out.println(result);

    }
	
/*    
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
                .build();

        QueryResponse response = client.query(queryReq);
        System.out.println(String.format("Query.Result.Count: %s", response.count()));
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
    
    
    public void itemDelete(String tableName, String keyRegionId, String keyPlayerId) {

        Table table = dynamoDB.getTable(tableName);

        try {

            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
            	.withPrimaryKey("Region", keyRegionId, "PlayerID", keyPlayerId)
                //.withConditionExpression("#ip = :val")
                //.withNameMap(new NameMap().with("#ip", "InPublication"))
                //.withValueMap(new ValueMap().withBoolean(":val", false))
                .withReturnValues(ReturnValue.ALL_OLD);

            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);

            // Check the response.
            System.out.println("Printing item that was deleted...");
            System.out.println(outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Error deleting item in " + tableName);
            System.err.println(e.getMessage());
        }
    }
    
	*/
    
    /*
    
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
