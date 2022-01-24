package com.xinyuz.tagging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeVolumesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.VolumeAttachment;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

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
 * 
 */
public class App {

    // TODO Put your region, tag key, value here
    static Region region = Region.AP_NORTHEAST_1;
    // static Region region = Region.CN_NORTHWEST_1;
    static HashMap<String, String> tags = new HashMap<>();
    static {
        tags.put("map-migrated", "d-server-20211020");
    }

    public static void main(String[] args) {
        Collection<Tag> tagList = new ArrayList<Tag>();
        for (Map.Entry<String, String> me : tags.entrySet()) {
            tagList.add(Tag.builder().key(me.getKey()).value(me.getValue()).build());
        }

        tagEc2(region, tagList);
        tagEBS(region, tagList);
        //tagES(region, tagList);
        //tagFSx(region, tagList);
        //tagS3();
    }

    private static void tagEBS(Region region, Collection<Tag> tagList) {
        Ec2Client               ec2      = Ec2Client.builder()
                .region(region)
                .build();
        DescribeVolumesRequest  request  = DescribeVolumesRequest.builder().build();
        DescribeVolumesResponse response = ec2.describeVolumes(request);

        if (response.volumes() == null || response.volumes().size() == 0) {
            System.out.println("No Instance found");
        }

        Collection<String> resourceList = new ArrayList<>();
        for (Volume reservation : response.volumes()) {
            for (VolumeAttachment ebsvol : reservation.attachments()) {
                System.out.printf(
                        "Found VolumeAttachment with id %s, " +
                                "device %s, " +
                                "state %s, " +
                                "instance id %s, " +
                                "attach time %s, " +
                                "\n",
                        ebsvol.volumeId(),
                        ebsvol.device(),
                        ebsvol.state().name(),
                        ebsvol.instanceId(),
                        ebsvol.attachTime());

                //instance.sdkFields().stream().forEach(x -> System.out.println(x.memberName() +"="));
                //instanceIdList.add(instance.instanceId());
                resourceList.add(ebsvol.volumeId());
            }
        }

        CreateTagsRequest  tagsRequest  = CreateTagsRequest.builder().resources(resourceList).tags(tagList).build();
        CreateTagsResponse tagsResponse = ec2.createTags(tagsRequest);
        System.out.printf("Tag resources %s, result is %s", resourceList, tagsResponse);
    }

    private static void tagEc2(Region region, Collection<Tag> tagList) {
        Ec2Client ec2 = Ec2Client.builder()
                .region(region)
                .build();

        Collection<String> resourceList = new ArrayList<>();
        resourceList.addAll(listEC2Instances(ec2));

        CreateTagsRequest request = CreateTagsRequest.builder().resources(resourceList).tags(tagList).build();
        if (resourceList != null && resourceList.size() > 0) {
            CreateTagsResponse response = ec2.createTags(request);
            System.out.printf("Tag resources %s, result is %s", resourceList, response);
        }

        ec2.close();
    }

    /**
     * 确定打标签的EC2实例的范围
     * 例子里的代码是通过launch-time，来查找昨天以来开启的资源
     * 
     * @param ec2
     * @return
     */
    private static Collection<? extends String> listEC2Instances(Ec2Client ec2) {
        Collection<String> instanceIdList = new ArrayList<>();
        String             nextToken      = null;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterdayStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        try {
            do {
                DescribeInstancesRequest request = DescribeInstancesRequest.builder().filters(Filter.builder().name("launch-time").values(yesterdayStr + "*").build()).maxResults(20).nextToken(nextToken).build();
                //DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
                DescribeInstancesResponse response = ec2.describeInstances(request);

                if (response.reservations() == null || response.reservations().size() == 0) {
                    System.out.println("No Instance found");
                }
                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
                        System.out.printf(
                                "Found Reservation with id %s, " +
                                        "AMI %s, " +
                                        "type %s, " +
                                        "state %s " +
                                        "and monitoring state %s" +
                                        "tags %s" +
                                        "Launch time %s" +
                                        "\n",
                                instance.instanceId(),
                                instance.imageId(),
                                instance.instanceType(),
                                instance.state().name(),
                                instance.monitoring().state(),
                                instance.tags(),
                                instance.launchTime());

                        instanceIdList.add(instance.instanceId());
                    }
                }
                nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return instanceIdList;
    }

    @SuppressWarnings("unused")
    private static void tagS3() {
        //ObjectTagging
        S3ClientBuilder s3b = S3Client.builder();
    }

    @SuppressWarnings("unused")
    private static void tagES(Region region, Collection<Tag> tagList) {
    }

    @SuppressWarnings("unused")
    private static void tagFSx(Region region, Collection<Tag> tagList) {
    }
}
