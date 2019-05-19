package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TableDeletion {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies7";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);
        deleteTable(table);
    }

    private static void deleteTable(Table table) {
        try {
            table.delete();
            logger.info(String.format("Deleting Table, it may take few seconds... (Table object: %s)", table));
            table.waitForDelete();
            logger.info(String.format("Deleted Table successfully."));
        }
        catch (Exception e) {
            logger.error("Unable to delete table!", e);
        }
    }
}
