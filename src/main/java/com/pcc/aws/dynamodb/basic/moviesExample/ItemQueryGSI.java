package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

public class ItemQueryGSI {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);

        queryItemsByTitle(table, "The Mummy");
        queryItemsByCountry(table, "United States");
        queryItemsByYearAndCountry(table, 2001, "United States");
        queryItemsByCountryBeforeYear(table, "United States", 2000);
    }

    /**
     * 回傳指定 title 的電影
     *
     * @param table
     * @param title
     */
    private static void queryItemsByTitle(Table table, String title) {
        Index index = table.getIndex("TitleIndex");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("title = :t")
                .withValueMap(new ValueMap().withString(":t", title));
        try {
            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with title=[%s] succeeded.", cnt, title));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }

    /**
     * 回傳指定 country 的電影
     *
     * @param table
     * @param country
     */
    private static void queryItemsByCountry(Table table, String country) {
        Index index = table.getIndex("CountryIndex");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("country = :c")
                .withValueMap(new ValueMap().withString(":c", country));
        try {
            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with country=[%s] succeeded.", cnt, country));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }

    /**
     * 回傳指定 year & country 的電影
     *
     * @param table
     * @param year
     * @param country
     */
    private static void queryItemsByYearAndCountry(Table table, int year, String country) {
        Index index = table.getIndex("YearCountryIndex");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("#yr = :yr and country = :c")
                .withNameMap(new NameMap().with("#yr", "year"))
                .withValueMap(new ValueMap()
                        .withNumber(":yr", year)
                        .withString(":c", country));
        try {
            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with year=[%d] and country=[%s] succeeded.", cnt, year, country));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }

    /**
     * 回傳指定 country，且小於指定年份的電影
     *
     * @param table
     * @param country
     * @param beforeYear
     */
    private static void queryItemsByCountryBeforeYear(Table table, String country, int beforeYear) {
        Index index = table.getIndex("CountryIndex");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("country = :c")
                .withFilterExpression("#yr < :yr")
                .withNameMap(new NameMap().with("#yr", "year"))
                .withValueMap(new ValueMap()
                        .withString(":c", country)
                        .withNumber(":yr", beforeYear));
        try {
            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iter = items.iterator();
            int cnt = 0;
            while (iter.hasNext()) {
                Item item = iter.next();
                logger.info(String.format("Query item %d: %s", ++cnt, item));
            }
            logger.info(String.format("Query %d items with country=[%s] and year < %d succeeded.", cnt, country, beforeYear));
        }
        catch (Exception e) {
            logger.error("Unable to query the table!", e);
        }
    }
}
