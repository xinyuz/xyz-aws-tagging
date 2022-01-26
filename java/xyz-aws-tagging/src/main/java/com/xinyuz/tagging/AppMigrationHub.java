package com.xinyuz.tagging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import software.amazon.awssdk.services.migrationhub.model.ListDiscoveredResourcesRequest;
import software.amazon.awssdk.services.migrationhub.model.ListDiscoveredResourcesResponse;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksRequest;
import software.amazon.awssdk.services.migrationhub.model.ListMigrationTasksResponse;
import software.amazon.awssdk.services.migrationhubconfig.MigrationHubConfigClient;
import software.amazon.awssdk.services.migrationhubconfig.model.GetHomeRegionRequest;
import software.amazon.awssdk.services.migrationhubconfig.model.GetHomeRegionResponse;

/**
 * https://docs.aws.amazon.com/migrationhub/latest/ug/api-reference.html
 * https://github.com/aws/aws-sdk-java
 */
public class AppMigrationHub {
    static Logger logger = LogManager.getLogger(AppMigrationHub.class);

    public static void main(String[] args) {
        Region region = Region.AP_NORTHEAST_1;

        MigrationHubClient client = MigrationHubClient.builder()
                .region(region)
                .build();
        listMigrationTask(client);

        MigrationHubConfigClient confclient = MigrationHubConfigClient.create();
        GetHomeRegionResponse    response   = confclient.getHomeRegion(GetHomeRegionRequest.builder().build());
        logger.info(response.toString());

        client.close();
    }

    @SuppressWarnings("unused")
    private static void listDiscoveredResources(MigrationHubClient client) {
        String nextToken = null;
        try {
            do {
                // DescribeMigrationTaskResponse response = client.describeMigrationTask(DescribeMigrationTaskRequest.builder().migrationTaskName("").progressUpdateStream("").build());

                ListDiscoveredResourcesRequest  request  = ListDiscoveredResourcesRequest.builder().maxResults(10).nextToken(nextToken).migrationTaskName("%s").progressUpdateStream("SMS").build();
                ListDiscoveredResourcesResponse response = client.listDiscoveredResources(request);

                System.out.println(response.discoveredResourceList());
                nextToken = response.nextToken();
            } while (nextToken != null);
        } catch (Ec2Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private static void listMigrationTask(MigrationHubClient client) {
        String nextToken = null;
        try {
            do {
                ListMigrationTasksRequest  request  = ListMigrationTasksRequest.builder().maxResults(10).nextToken(nextToken).build();
                ListMigrationTasksResponse response = client.listMigrationTasks(request);

                System.out.println(response.migrationTaskSummaryList());
                nextToken = response.nextToken();
            } while (nextToken != null);
        } catch (Ec2Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
