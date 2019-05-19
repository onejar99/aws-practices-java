package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Note:
 * - base table 或 index 所用到的 partition key 和 sort key，都需要在 Attribute definitions 定義
 * - 然後 create table 時提供 Attribute definitions (就像一種 reference)
 * - 但 GSI 所 Project 的 Attribute 不用定義
 */
public class TableCreation {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        //createTable(dynamoDB, tableName);
        createTableWithGSI(dynamoDB, tableName);
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

    private static Table createTableWithGSI(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            // Attribute definitions
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(
                    new AttributeDefinition("year", ScalarAttributeType.N),
                    new AttributeDefinition("title", ScalarAttributeType.S),
                    new AttributeDefinition("country", ScalarAttributeType.S));

            // Table key schema
            List<KeySchemaElement> keySchema = Arrays.asList(
                    new KeySchemaElement("year", KeyType.HASH),
                    new KeySchemaElement("title", KeyType.RANGE));

            // Secondary Indexes 1: title
            List<KeySchemaElement> titleIndexKeySchema = Arrays.asList(
                    new KeySchemaElement("title", KeyType.HASH));
            GlobalSecondaryIndex titleIndex = new GlobalSecondaryIndex()
                    .withIndexName("TitleIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));
            titleIndex.setKeySchema(titleIndexKeySchema);

            // Secondary Indexes 2: country
            List<KeySchemaElement> countryIndexKeySchema = Arrays.asList(
                    new KeySchemaElement("country", KeyType.HASH));
            GlobalSecondaryIndex countryIndex = new GlobalSecondaryIndex()
                    .withIndexName("CountryIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    .withProjection(new Projection()
                            .withProjectionType(ProjectionType.INCLUDE)
                            .withNonKeyAttributes(Arrays.asList("release_uts")));
            countryIndex.setKeySchema(countryIndexKeySchema);

            // Secondary Indexes 3: year-country
            List<KeySchemaElement> yearCountryIndexKeySchema = Arrays.asList(
                    new KeySchemaElement("year", KeyType.HASH),
                    new KeySchemaElement("country", KeyType.RANGE));
            GlobalSecondaryIndex yearCountryIndex = new GlobalSecondaryIndex()
                    .withIndexName("YearCountryIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                    .withProjection(new Projection().withProjectionType(ProjectionType.KEYS_ONLY));
            yearCountryIndex.setKeySchema(yearCountryIndexKeySchema);

            // create table
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(tableName)
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L))
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(keySchema)
                    .withGlobalSecondaryIndexes(titleIndex, countryIndex, yearCountryIndex);

            table = dynamoDB.createTable(createTableRequest);
            logger.info(String.format("Creating Table, it may take few seconds... (Table status: %s)", table.getDescription().getTableStatus()));
            table.waitForActive(); // wait few seconds for creating
            logger.info(String.format("Created and activated Table successfully. (Table status: %s)", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            logger.error("Unable to create table!", e);
        }
        return table;
    }

    private static Table createTable_verVerbose(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            // Table key schema
            KeySchemaElement partitionKey = new KeySchemaElement("year", KeyType.HASH);
            KeySchemaElement sortKey = new KeySchemaElement("title", KeyType.RANGE);
            List<KeySchemaElement> keySchema = Arrays.asList(partitionKey, sortKey);

            // Attribute definitions
            AttributeDefinition attrYear = new AttributeDefinition("year", ScalarAttributeType.N);
            AttributeDefinition attrTitle = new AttributeDefinition("title", ScalarAttributeType.S);
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(attrYear, attrTitle);

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

    private static Table createTableWithGSI_verVerbose(DynamoDB dynamoDB, String tableName) {
        Table table = null;
        try {
            // Table key schema
            KeySchemaElement partitionKey = new KeySchemaElement()
                    .withAttributeName("year")
                    .withKeyType(KeyType.HASH);
            KeySchemaElement sortKey = new KeySchemaElement()
                    .withAttributeName("title")
                    .withKeyType(KeyType.RANGE);
            List<KeySchemaElement> keySchema = Arrays.asList(partitionKey, sortKey);

            // Attribute definitions
            AttributeDefinition attrYear = new AttributeDefinition()
                    .withAttributeName("year")
                    .withAttributeType(ScalarAttributeType.N);
            AttributeDefinition attrTitle = new AttributeDefinition()
                    .withAttributeName("title")
                    .withAttributeType(ScalarAttributeType.S);
            AttributeDefinition attrCountry = new AttributeDefinition()
                    .withAttributeName("country")
                    .withAttributeType(ScalarAttributeType.S);
            List<AttributeDefinition> attributeDefinitions = Arrays.asList(attrYear, attrTitle, attrCountry);

            // Secondary Indexes 1: title
            GlobalSecondaryIndex titleIndex = new GlobalSecondaryIndex()
                    .withIndexName("TitleIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));
            List<KeySchemaElement> titleIndexKeySchema = new ArrayList<>();
            titleIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName("title")
                    .withKeyType(KeyType.HASH));
            titleIndex.setKeySchema(titleIndexKeySchema);

            // Secondary Indexes 2: country
            GlobalSecondaryIndex countryIndex = new GlobalSecondaryIndex()
                    .withIndexName("CountryIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                    .withProjection(new Projection()
                            .withProjectionType(ProjectionType.INCLUDE)
                            .withNonKeyAttributes(Arrays.asList("release_uts")));
            List<KeySchemaElement> countryIndexKeySchema = new ArrayList<>();
            countryIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName("country")
                    .withKeyType(KeyType.HASH));
            countryIndex.setKeySchema(countryIndexKeySchema);

            // Secondary Indexes 3: year-country
            GlobalSecondaryIndex yearCountryIndex = new GlobalSecondaryIndex()
                    .withIndexName("YearCountryIndex")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                    .withProjection(new Projection()
                            .withProjectionType(ProjectionType.KEYS_ONLY));
            List<KeySchemaElement> yearCountryIndexKeySchema = new ArrayList<>();
            yearCountryIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName("year")
                    .withKeyType(KeyType.HASH));
            yearCountryIndexKeySchema.add(new KeySchemaElement()
                    .withAttributeName("country")
                    .withKeyType(KeyType.RANGE));
            yearCountryIndex.setKeySchema(yearCountryIndexKeySchema);

            // ProvisionedThroughput
            ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(10L, 10L);

            // create table
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(tableName)
                    .withProvisionedThroughput(provisionedThroughput)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(keySchema)
                    .withGlobalSecondaryIndexes(titleIndex, countryIndex, yearCountryIndex);

            table = dynamoDB.createTable(createTableRequest);
            logger.info(String.format("Creating Table, it may take few seconds... (Table status: %s)", table.getDescription().getTableStatus()));
            table.waitForActive(); // wait few seconds for creating
            logger.info(String.format("Created and activated Table successfully. (Table status: %s)", table.getDescription().getTableStatus()));
        } catch (Exception e) {
            logger.error("Unable to create table!", e);
        }
        return table;
    }
}
