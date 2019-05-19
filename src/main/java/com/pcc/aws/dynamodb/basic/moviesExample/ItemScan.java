package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

/**
 * Note:
 * - 無法提供 pk 時使用 (Note: best practice 應該用 index 去 query)
 * - scan 會讀取整個 table 中的每個 item 並回傳.
 * - 可以提供 filter_expression，只傳回符合條件的 item (不過在掃描過整個 table 之後才會套用篩選條件).
 *
 * - FilterExpression: 指定篩選條件，只傳回符合該條件的 items
 * - ProjectionExpression: 指定想要包含在回傳結果中的 attributes.
 */
public class ItemScan {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies9";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);

        scanAllItems(table);
        scanItemsByYearRange(table, 1990, 2012);
        scanItemsByTitleStartingWith(table, "A", "T");
        scanItemsByLanguage(table, "English");
        scanItemsByRatingThreshold(table, 9.6f);
    }

    private static void scanAllItems(Table table) {
        try {
            ItemCollection<ScanOutcome> items = table.scan(new ScanSpec());
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Scan %d items succeeded.", cnt));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }

    /**
     * 回傳指定年份 range 的電影 (Note: search by pk, best practice 應該用 query 而非 scan)
     *
     * @param table
     * @param yrStart
     * @param yrEnd
     */
    private static void scanItemsByYearRange(Table table, int yrStart, int yrEnd) {
        ValueMap valueMap = new ValueMap()
                .withNumber(":yr_start", yrStart).withNumber(":yr_end", yrEnd);
        NameMap nameMap = new NameMap().with("#yr", "year");
        ScanSpec scanSpec = new ScanSpec()
                .withProjectionExpression("#yr, title")
                .withFilterExpression("#yr between :yr_start and :yr_end")
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Scan %d items with year %d ~ %d succeeded.", cnt, yrStart, yrEnd));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }

    /**
     * 回傳 title 開頭字母在指定範圍的電影 (e.g., A~T=開頭是A~S)
     * (Note: search by sort key, best practice 應該用 index 去 query)
     *
     * @param table
     * @param letter1
     * @param letter2
     */
    private static void scanItemsByTitleStartingWith(Table table, String letter1, String letter2) {
        ValueMap valueMap = new ValueMap()
                .withString(":letter1", letter1)
                .withString(":letter2", letter2);
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("title between :letter1 and :letter2")
                .withValueMap(valueMap);

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item.toJSONPretty()));
            }
            logger.info(String.format(String.format("Scan %d items with title %s-%s succeeded", cnt, letter1, letter2)));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }

    /**
     * 回傳指定 language 的電影
     *
     * @param table
     * @param lang
     */
    private static void scanItemsByLanguage(Table table, String lang) {
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("detail_info.#lang = :lang")
                .withNameMap(new NameMap().with("#lang", "language"))
                .withValueMap(new ValueMap().withString(":lang", lang));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item.toJSONPretty()));
            }
            logger.info(String.format("Scan %d items with %s language succeeded.", cnt, lang));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }

    /**
     * 回傳指定 rating 以上的電影
     *
     * @param table
     * @param rating
     */
    private static void scanItemsByRatingThreshold(Table table, float rating) {
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("detail_info.rating >= :rating")
                .withValueMap(new ValueMap().withNumber(":rating", rating));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Scan item %d: %s", ++cnt, item.toJSONPretty()));
            }
            logger.info(String.format("Scan %d items with rating >= %f succeeded.", cnt, rating));
        }
        catch (Exception e) {
            logger.error("Unable to scan the table!", e);
        }
    }
}
