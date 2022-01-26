package com.xinyuz.tagging;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.AddTagsToResourceRequest;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.Tag;

/**
 * doc
 * https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_Tagging.html
 * 
 * cli
 * https://docs.aws.amazon.com/cli/latest/reference/rds/add-tags-to-resource.html
 * 
 * sdk
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/rds/RdsClient.html#addTagsToResource-software.amazon.awssdk.services.rds.model.AddTagsToResourceRequest-
 * 
 * @author zhouandr
 *
 */
public class TagRDS {

    public static void tagRds() {
        RdsClient rdsClient = RdsClient.builder()
                .region(TagMain.region)
                .build();

        
        // Tag db instance
        DescribeDbInstancesResponse dbInstancesRep = rdsClient.describeDBInstances();
        for (DBInstance dbinstance : dbInstancesRep.dbInstances()) {
            String resourceName = dbinstance.dbInstanceArn();
            System.out.printf("RDS, %s.\n", resourceName);
            AddTagsToResourceRequest addTagsToResourceRequest = AddTagsToResourceRequest.builder().resourceName(resourceName)
                    .tags(translateTagsToSdk(TagMain.tags))
                    .build();
            rdsClient.addTagsToResource(addTagsToResourceRequest);
        }
    }

    public static Collection<Tag> translateTagsToSdk(Map<String, String> tags) {
        return tags.entrySet()
                .stream()
                .map(entry -> Tag.builder()
                        .key(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
