package com.xinyuz.tagging;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.applicationdiscovery.ApplicationDiscoveryClient;
import software.amazon.awssdk.services.applicationdiscovery.model.DescribeExportTasksRequest;
import software.amazon.awssdk.services.applicationdiscovery.model.DescribeExportTasksResponse;

/**
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/applicationdiscovery/package-frame.html
 */
public class AppApplicationDiscovery {

    public static void main(String[] args) {
        Region region = Region.AP_NORTHEAST_1;

        ApplicationDiscoveryClient client = ApplicationDiscoveryClient.builder().region(region).build();
        DescribeExportTasksResponse response = client.describeExportTasks(DescribeExportTasksRequest.builder().build());
        System.out.println(response);

        client.close();
    }

}
