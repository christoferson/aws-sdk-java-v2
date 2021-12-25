package demo.aws.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputDescription;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

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
 
	

	public void tableDescribe(String tableName) {

		DescribeTableRequest request = DescribeTableRequest.builder().tableName(tableName).build();

		try {
			TableDescription tableInfo = client.describeTable(request).table();

			if (tableInfo != null) {
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
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		System.out.println("\nDone!");

	}
	
//	{Profession=AttributeValue(S=Hunter), Race=AttributeValue(S=Human), CharacterName=AttributeValue(S=Name1), Region=AttributeValue(S=US)}
//	{Profession=AttributeValue(S=Hunter), Race=AttributeValue(S=Human), CharacterName=AttributeValue(S=Name2), Region=AttributeValue(S=TW)}
    public void tableScan(String tableName) {
        try {
        
            ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .build();

            ScanResponse response = client.scan(scanRequest);
            for (Map<String, AttributeValue> item : response.items()) {
//                Set<String> keys = item.keySet();
//                for (String key : keys) {
//                    System.out.println ("The key name is "+key +"\n" );
//                    System.out.println("The value is "+item.get(key).s());
//                }
                System.out.println(item);
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void itemQuery(String tableName) throws DynamoDbException {

        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNames = new HashMap<String,String>();
        attrNames.put("#key_region", "Region");

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues = new HashMap<String,AttributeValue>();
        attrValues.put(":"+"v_region", AttributeValue.builder().s("US").build());
        attrValues.put(":"+"character_name", AttributeValue.builder().s("Name1").build());
        
        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#key_region = :v_region and CharacterName = :character_name")
                //.keyConditionExpression("#key_region = :v_region")
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
	
	
	public void itemRetrieve(String tableName, String partitionId, String sortId) {
 
        HashMap<String,AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

        keyToGet.put("Region", AttributeValue.builder().s("US").build());

        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = client.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", "sss");
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
/*	
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
