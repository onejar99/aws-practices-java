package com.pcc.aws.dynamodb.basic.moviesExample;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

/**
 * Note:
 * - Via Table Description
 */
public class GSIGet {
    private static Log logger = LogFactory.getLog(TableCreation.class);

    public static void main(String[] args) {
        final String tableName = "Movies93";
        DynamoDB dynamoDB = ConfigServiceClient.createDynamoDBInstance();
        Table table = dynamoDB.getTable(tableName);
        descGSI(table);
    }

    private static void descGSI(Table table) {
        TableDescription tableDesc = table.describe();
        Iterator<GlobalSecondaryIndexDescription> gsiIter = tableDesc.getGlobalSecondaryIndexes().iterator();
        while (gsiIter.hasNext()) {
            GlobalSecondaryIndexDescription gsiDesc = gsiIter.next();
            logger.info(String.format("Info for GSI IndexName=[%s]:", gsiDesc.getIndexName()));

            // Get key schema
            Iterator<KeySchemaElement> kseIter = gsiDesc.getKeySchema().iterator();
            while (kseIter.hasNext()) {
                KeySchemaElement kse = kseIter.next();
                logger.info(String.format("\t - AttributeName: [%s] KeyType=[%s]", kse.getAttributeName(), kse.getKeyType()));
            }

            // Get projection
            Projection projection = gsiDesc.getProjection();
            logger.info(String.format("\t - The projection type is: [%s]", projection.getProjectionType()));
            if (ProjectionType.INCLUDE.toString().equals(projection.getProjectionType())) {
                logger.info(String.format("\t\t The %d non-key projected attributes are: %s",
                        projection.getNonKeyAttributes().size(), projection.getNonKeyAttributes()));
            }
        }
    }
}
