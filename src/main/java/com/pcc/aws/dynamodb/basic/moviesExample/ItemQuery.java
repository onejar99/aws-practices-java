package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

/**
 * Note:
 * - 至少要已知 partition key (condition 必須指定明確的 partition key, e.g., `#yr = :val`)
 * - 可搭配 sort key 或選擇不要，sort key 的 condition 可以使用條件判斷 (e.g., `>`, `<`, `between`)
 * - (sort key 就像 partition key 附屬品，如果沒有建 index，無法只 query sort key 而無指定 partitiona key)
 * - `withKeyConditionExpression` 只有 key 和 index 才能下
 * - 但其他 attribute 可以用 `withFilterExpression`
 *
 * - Exception Example 1: condition 裡沒有指定 partition key
 *      - e.g., only `title = :val`
 *      - `com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException: Query condition missed key schema element`
 *
 * - Exception Example 2: 沒有指定明確的 partition key
 *      - e.g., `#yr < :val`
 *      - com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException: Query key condition not supported
 */
public class ItemQuery {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies9";
        DynamoDB dynamoDB = ConfigServiceClient.buildDynamoDBClient();
        Table table = dynamoDB.getTable(tableName);

        queryItemsByYear(table, 2001);
        queryItemsByYearAndTitleAndRating(table, 2001, "S", "Z", 6f);
    }

    /**
     * 回傳指定年份的電影
     *
     * @param table
     * @param year
     */
    private static void queryItemsByYear(Table table, int year) {
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("#yr = :yr")
                .withNameMap(new NameMap().with("#yr", "year"))
                .withValueMap(new ValueMap().withNumber(":yr", year));

        try {
            ItemCollection<QueryOutcome> items = table.query(querySpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with year=[%d] succeeded.", cnt, year));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }

    /**
     * 回傳指定年份 + title 開頭字母在指定範圍(e.g., A~T=開頭是A~S)+ rating 大於等於指定值的電影
     *
     * @param table
     * @param year
     * @param letter1
     * @param letter2
     * @param rating
     */
    private static void queryItemsByYearAndTitleAndRating(Table table, int year, String letter1, String letter2, float rating) {
        QuerySpec querySpec = new QuerySpec()
                .withProjectionExpression("#yr, title, detail_info.box_office, detail_info.#r[0], detail_info.rating")
                .withKeyConditionExpression("#yr = :yr and title between :letter1 and :letter2")
                .withFilterExpression("detail_info.rating >= :rating")
                .withNameMap(new NameMap()
                        .with("#yr", "year")
                        .with("#r", "roles"))
                .withValueMap(new ValueMap()
                        .withNumber(":yr", year)
                        .withString(":letter1", letter1)
                        .withString(":letter2", letter2)
                        .withNumber(":rating", rating));

        try {
            ItemCollection<QueryOutcome> items = table.query(querySpec);
            Iterator<Item> iterator = items.iterator();
            int cnt = 0;
            while (iterator.hasNext()) {
                Item item = iterator.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with year=%d and titles %s-%s succeeded", cnt, year, letter1, letter2));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }
}
