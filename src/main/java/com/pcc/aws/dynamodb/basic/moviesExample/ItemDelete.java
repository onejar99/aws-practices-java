package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Note:
 * - 刪除指定的一筆 item (必須指定明確 primary key)，可附帶 condition 但非必要
 * - 無論是否有刪除到東西，`outcome.getItem()` 都是 `null`
 * - 只指定 primary key，無 condition 時: 若 item 不存在不會拋錯
 * - 若有指定 `withConditionExpression` 時:
 *      - (1)如果 condition not passed，或 (2)該 primary key 的 item 不存在，會拋 exception
 *      - Exception: `com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException: The conditional request failed`
 */
public class ItemDelete {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);
        deleteItemWithCondition(table, 2019, "Avengers: Endgame", 9.5f);
    }

    private static void deleteItemWithCondition(Table table, int year, String title, float ratingThreshold) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("year", year, "title", title))
                .withConditionExpression("detail_info.rating < :val")
                .withValueMap(new ValueMap().withNumber(":val", ratingThreshold));
        try {
            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
            logger.info(String.format("DeleteItem succeeded: %s", outcome.getItem()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to delete item: year=[%d] title=[%s]", year, title), e);
        }
    }
}
