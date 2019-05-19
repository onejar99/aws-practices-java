package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class ConfigServiceClient {
    private static final Regions CONFIG_REGIONS = Regions.US_EAST_2;

    public static DynamoDB createDynamoDBInstance() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(CONFIG_REGIONS)
                .build();
        return new DynamoDB(client);
    }
}
