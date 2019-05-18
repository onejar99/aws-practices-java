package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Note:
 * - 用完整 primary key 讀取特定一筆 item
 */
public class ItemRead {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies9";
        DynamoDB dynamoDB = ConfigServiceClient.buildDynamoDBClient();
        Table table = dynamoDB.getTable(tableName);
        readItem(table, 2019, "Avengers: Endgame");
    }

    private static void readItem(Table table, int year, String title) {
        GetItemSpec spec = new GetItemSpec()
                .withPrimaryKey("year", year, "title", title);

        try {
            Item item = table.getItem(spec);
            logger.info(String.format("GetItem succeeded: %s", item.toJSONPretty()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to read item: year=[%d] title=[%s]", year, title), e);
        }
    }
}
