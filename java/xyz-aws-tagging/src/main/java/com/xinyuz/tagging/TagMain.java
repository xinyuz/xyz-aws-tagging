package com.xinyuz.tagging;

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Tag;

/**
 * 
 * EC2部分
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/model/CreateTagsRequest.html
 * https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/DescribeInstancesRequest.html
 * https://github.com/aws/aws-cli/issues/1209
 * https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/describe-instances.html
 * 
 * S3 部分
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/model/Tagging.html
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-s3-objects.html
 * https://github.com/aws/aws-sdk-java-v2/blob/master/services/s3/src/it/java/software/amazon/awssdk/services/s3/S3IntegrationTestBase.java
 * https://github.com/aws/aws-sdk-java-v2/blob/master/services/s3/src/it/java/software/amazon/awssdk/services/s3/ObjectTaggingIntegrationTest.java
 * 
 * tag resource
 * https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-resource-tags.html
 * 
 * Junit 5
 * https://junit.org/junit5/docs/current/user-guide/
 * 
 * 
 */
public class TagMain {

    // TODO Put your region, tag key, value here
    static Region                  region = Region.AP_NORTHEAST_1;
    static HashMap<String, String> tags   = new HashMap<>();
    static {
        tags.put("map-migrated", "d-server-20211020");
    }

    static final Logger logger = LogManager.getLogger(TagMain.class);

    public static void main(String[] args) {
        Collection<Tag> tagList = TagEc2.translateTagsToSdk(tags);

        TagEc2.tagEc2(region, tagList);
        TagEc2.tagEBS(region, tagList);
        TagRDS.tagRds();
    }
}
