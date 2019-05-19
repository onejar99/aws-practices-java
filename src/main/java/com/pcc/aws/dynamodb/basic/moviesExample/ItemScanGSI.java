package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

public class ItemScanGSI {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);
        scanAllItemsByGSI(table, "YearCountryIndex");
    }

    private static void scanAllItemsByGSI(Table table, String indexName) {
        Index index = table.getIndex(indexName);
        try {
            ItemCollection<ScanOutcome> items = index.scan(new ScanSpec());
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Scan %d items of GSI=[%s] succeeded.", cnt, indexName));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }
}
