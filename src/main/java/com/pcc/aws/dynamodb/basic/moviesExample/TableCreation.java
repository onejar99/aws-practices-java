package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;
import java.util.List;

public class TableCreation {
    public static void main(String[] args) {
        final String tableName = "Movies7";
        DynamoDB dynamoDB = ConfigServiceClient.buildDynamoDBClient();
        createTable(dynamoDB, tableName);
    }

    private static Table createTable(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            table = dynamoDB.createTable(tableName,
                    Arrays.asList(
                            new KeySchemaElement("year", KeyType.HASH), // Partition key
                            new KeySchemaElement("title", KeyType.RANGE)), // Sort key
                    Arrays.asList(
                            new AttributeDefinition("year", ScalarAttributeType.N),
                            new AttributeDefinition("title", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            System.out.println(String.format("Creating Table, it may take few seconds... (Table status: %s)", table.getDescription().getTableStatus()));
            table.waitForActive();
            System.out.println(String.format("Created and activated Table successfully. (Table status: %s)", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
        return table;
    }

    private static Table createTable_verVerbose(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            // keySchema
            KeySchemaElement partitionKey = new KeySchemaElement("year", KeyType.HASH);
            KeySchemaElement sortKey = new KeySchemaElement("title", KeyType.RANGE);
            List<KeySchemaElement> keySchema = Arrays.asList(partitionKey, sortKey);

            // attributeDefinitions
            AttributeDefinition AttrYear = new AttributeDefinition("year", ScalarAttributeType.N);
            AttributeDefinition attrTitle = new AttributeDefinition("title", ScalarAttributeType.S);
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(AttrYear, attrTitle);

            // ProvisionedThroughput
            ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(10L, 10L);

            // create table
            table = dynamoDB.createTable(tableName, keySchema, attributeDefinitions, provisionedThroughput);
            System.out.println(String.format("Created Table, Table status: %s", table.getDescription().getTableStatus()));
            table.waitForActive(); // wait few seconds for creating
            System.out.println(String.format("Activated Table, Table status: %s", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
        return table;
    }
}
