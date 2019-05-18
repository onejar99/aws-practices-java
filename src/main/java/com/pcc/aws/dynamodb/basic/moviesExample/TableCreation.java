package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

public class TableCreation {
    private static Log logger = LogFactory.getLog(TableCreation.class);

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
            logger.info(String.format("Creating Table, it may take few seconds... (Table status: %s)", table.getDescription().getTableStatus()));
            table.waitForActive();
            logger.info(String.format("Created and activated Table successfully. (Table status: %s)", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            logger.error("Unable to create table!", e);
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
            logger.info(String.format("Creating Table, it may take few seconds... (Table status: %s)", table.getDescription().getTableStatus()));
            table.waitForActive(); // wait few seconds for creating
            logger.info(String.format("Created and activated Table successfully. (Table status: %s)", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            logger.error("Unable to create table!", e);
        }
        return table;
    }
}
