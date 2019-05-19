package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Note:
 * - 更新指定的一筆 item (必須指定明確 primary key，且必須存在)
 * - ReturnValues 參數:
 *      - UPDATED_NEW: 指示 DynamoDB 只傳回更新後的屬性
 * - 如果使用 Incrementing an Atomic Counter，該 attribute 必須已存在
 *      - 否則會 exception: `com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException: The provided expression refers to an attribute that does not exist in the item`
 * - `withConditionExpression`:
 *      - 和 SQL 的 `where` 概念不太一樣，如果 condition not passed 會拋 exception
 *      - Exception: `com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException: The conditional request failed`
 */
public class ItemUpdate {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);

        logger.info("======== Original Item ========");
        ItemAddOrReplace.addOrReplaceOneDummyItem(table);
        int year = 2019;
        String title = "Avengers: Endgame";
        ItemRead.readItem(table, year, title);

        logger.info("======== Update Item   ========");
        updateItem(table, year, title, "United States", 10, Arrays.asList("Anthony Russo", "Joe Russo"));
        ItemRead.readItem(table, year, title);

        logger.info("======== Update Item for Adding Profit (by incrementing an atomic counter) ========");
        updateItemForAddingProfit(table, year, title, 5);
        ItemRead.readItem(table, year, title);

        logger.info("======== Update Item for Disappearing a Hero (with conditions hero > 2)  ========");
        updateItemForDisappearingHero(table, year, title);
        ItemRead.readItem(table, year, title);
    }

    private static void updateItem(Table table, int year, String title, String country, int profitMillions, List<String> directors) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("year", year, "title", title)
                .withUpdateExpression("set country = :c, detail_info.profit_millions=:p, detail_info.directors=:d")
                .withValueMap(new ValueMap()
                        .withString(":c", country)
                        .withNumber(":p", profitMillions)
                        .withList(":d", directors))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            logger.info(String.format("UpdateItem succeeded: %s", outcome.getItem().toJSONPretty()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to update item: year=[%d] title=[%s]", year, title), e);
        }
    }

    /**
     * Add profit (by incrementing an atomic counter)
     *
     * @param table
     * @param year
     * @param title
     * @param addProfit
     */
    private static void updateItemForAddingProfit(Table table, int year, String title, int addProfit) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("year", year, "title", title)
                .withUpdateExpression("set detail_info.profit_millions = detail_info.profit_millions + :val")
                .withValueMap(new ValueMap().withNumber(":val", addProfit))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            logger.info(String.format("UpdateItem succeeded: %s", outcome.getItem().toJSONPretty()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to update item: year=[%d] title=[%s]", year, title), e);
        }
    }

    /**
     * Remove a role (with conditions)
     *
     * @param table
     * @param year
     * @param title
     */
    private static void updateItemForDisappearingHero(Table table, int year, String title) {
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("year", year, "title", title)
                .withUpdateExpression("remove detail_info.#r[0]")
                .withConditionExpression("size(detail_info.#r) > :num")
                .withNameMap(new NameMap().with("#r", "roles")) // roles is a reserved keyword
                .withValueMap(new ValueMap().withNumber(":num", 2))
                .withReturnValues(ReturnValue.UPDATED_NEW);
        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            logger.info(String.format("UpdateItem succeeded: %s", outcome.getItem().toJSONPretty()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to update item: year=[%d] title=[%s]", year, title), e);
        }
    }
}
