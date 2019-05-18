package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ItemAddOrReplace {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies9";
        DynamoDB dynamoDB = ConfigServiceClient.buildDynamoDBClient();
        Table table = dynamoDB.getTable(tableName);

        // (1) add/update one item
        // dummy data
        int year = 2019;
        String title = "Avengers: Endgame";
        final Map<String, Object> detailInfoMap = new HashMap<>();
        detailInfoMap.put("roles", Arrays.asList("Iron Man","Captain America","Hulk","Black Widow"));
        detailInfoMap.put("rating", 8.32);

        addOrUpdateItem(table, year, title, detailInfoMap);

        // (2) import data from a Json file
        final Path filePath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "movieData.json");
        importDataFromMovieJson(table, filePath.toString());
    }

    private static void importDataFromMovieJson(Table table, String filePath){
        /* Using open source `Jackson` library to process JSON.
           `Jackson` is included in AWS SDK for Java, no need to install it separately. */
        int procCnt = 0;
        try {
            JsonParser parser = new JsonFactory().createParser(new File(filePath));
            JsonNode rootNode = new ObjectMapper().readTree(parser);
            Iterator<JsonNode> iter = rootNode.iterator();
            ObjectNode currNode;

            while (iter.hasNext()) {
                currNode = (ObjectNode) iter.next();
                int year = currNode.path("year").asInt();
                String title = currNode.path("title").asText();
                String detailInfoJson = currNode.path("detail_info").toString();

                addOrUpdateItem(table, year, title, detailInfoJson);
                procCnt++;
            }
            logger.info(String.format("Total %d items imported.", procCnt));
            parser.close();
        } catch (IOException e) {
            logger.error("Something wrong when importing movie data!", e);
        }
    }

    private static void addOrUpdateItem(Table table, int year, String title, Map<String, Object> infoMap) {
        Item item = new Item()
                .withPrimaryKey("year", year, "title", title)
                .withMap("detail_info", infoMap);
        putItemToTable(table, item);
    }

    private static void addOrUpdateItem_verVerbose(Table table, int year, String title, Map<String, Object> infoMap) {
        Item item = new Item();
        item = item.withPrimaryKey("year", year, "title", title);
        item = item.withMap("detail_info", infoMap);
        putItemToTable(table, item);
    }

    private static void addOrUpdateItem(Table table, int year, String title, String infoJson) {
        Item item = new Item()
                .withPrimaryKey("year", year, "title", title)
                .withJSON("detail_info", infoJson);
        putItemToTable(table, item);
    }

    private static void putItemToTable(Table table, Item item) {
        try {
            PutItemOutcome outcome = table.putItem(item);
            logger.info(String.format("Put item year=[%d] title=[%s] succeeded: %s",
                    item.getInt("year"), item.getString("title"),
                    outcome.getPutItemResult()));
        }
        catch (Exception e) {
            logger.error(String.format("Unable to put item: year=[%d] title=[%s]",
                    item.getInt("year"), item.getString("title")), e);
        }
    }
}
