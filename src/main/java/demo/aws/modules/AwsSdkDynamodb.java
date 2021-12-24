package demo.aws.modules;

import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;

public class AwsSdkDynamodb {

	private DynamoDbClient client;

	public AwsSdkDynamodb(AwsCredentialsProvider credentialsProvider, Region region) {
		
		this.client = DynamoDbClient.builder()
				  .credentialsProvider(credentialsProvider)
				  .region(region)
				  .build();

	}
	
	public void tableList() {

        String lastName = null;

        try {

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
			
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
        System.out.println("\nDone!");
    }
 
	
/*	
	public void tableDescribe(String tableName) {

        System.out.println("Describing " + tableName);

        TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
        System.out.format(
            "Name: %s:\n" + "Status: %s \n" + "Provisioned Throughput (read capacity units/sec): %d \n"
                + "Provisioned Throughput (write capacity units/sec): %d \n",
            tableDescription.getTableName(), tableDescription.getTableStatus(),
            tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
            tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }
	
	
	public void itemRetrieve(String tableName, String keyRegionId, String keyPlayerId) {
        Table table = dynamoDB.getTable(tableName);

        try {
        	GetItemSpec i = new GetItemSpec();
        	i.withPrimaryKey("Region", keyRegionId, "PlayerID", keyPlayerId);
            //Item item = table.getItem("Region", keyRegionId, "PlayerID", keyPlayerId, "PlayerID", null);
        	Item item = table.getItem("Region", keyRegionId, "PlayerID", keyPlayerId, null, null);
            
            System.out.println("Printing item after retrieving it....");
            //System.out.println(item.toJSONPretty());
            System.out.println(item);

        }
        catch (Exception e) {
            System.err.println("GetItem failed.");
            System.err.println(e.getMessage());
        }

    }
	
    public void itemRegister(String tableName, String keyRegionId, String keyPlayerId) {

        Table table = dynamoDB.getTable(tableName);
        try {

            Item item = new Item()
            		.withPrimaryKey("Region", keyRegionId, "PlayerID", keyPlayerId)
            		.withString("Level", "1");
            // Without the condition, existing record will be overwritten
            table.putItem(item, "attribute_not_exists(PlayerID)", null, null);

            System.out.println("Registered: " + item);

        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());
        }

    }
    
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
    
    public void itemQuery(String tableName) {

		Table table = dynamoDB.getTable(tableName);

		QuerySpec spec = new QuerySpec()
				.withKeyConditionExpression("Region = :v_id")
				.withValueMap(new ValueMap().withString(":v_id", "Conan"));
				//.withFilterExpression("Region = :v_region and PlayerID = :v_id")
				//.withValueMap(new ValueMap().withString(":v_id", "Conan").withString("v_region", "US"));

		ItemCollection<QueryOutcome> items = table.query(spec);

		int count = 0;
		for (Item item : items) {
			System.out.println(item);
			count++;
		}
		if (count == 0) {
			System.out.println("No Matches");
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
    
    public void tableScan(String tableName) {
    	System.out.println("Scan Table");
    	ScanRequest scanRequest = new ScanRequest()
    	    .withTableName(tableName);

    	ScanResult result = client.scan(scanRequest);
    	for (Map<String, AttributeValue> item : result.getItems()){
    		System.out.println(item);
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
