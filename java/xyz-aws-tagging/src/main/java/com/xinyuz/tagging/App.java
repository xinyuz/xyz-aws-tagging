package com.xinyuz.tagging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateTagsRequest;
import software.amazon.awssdk.services.ec2.model.CreateTagsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Tag;

/**
 * https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/ec2/model/CreateTagsRequest.html
 *
 */
public class App {

    public static void main(String[] args) {
        // TODO Put your region, tag key, value here
        Region region = Region.AP_NORTHEAST_1;
        HashMap<String, String> tagmap = new HashMap<>();

        Ec2Client ec2 = Ec2Client.builder()
                                 .region(region)
                                 .build();

        Collection<Tag> tagList = new ArrayList<Tag>();
        for (Map.Entry<String, String> me : tagmap.entrySet()) {
            tagList.add(Tag.builder().key(me.getKey()).value(me.getValue()).build());
        }
        Collection<String> resourceList = new ArrayList<>();

        resourceList.addAll(listEC2Instances(ec2));
        tagResources(ec2, resourceList, tagList);

        ec2.close();
    }

    private static void tagResources(Ec2Client ec2, Collection<String> resourceList, Collection<Tag> tagList) {
        CreateTagsRequest request = CreateTagsRequest.builder().resources(resourceList).tags(tagList).build();
        CreateTagsResponse response = ec2.createTags(request);

        System.out.printf("Tag resources %s, result is %s", resourceList, response);
    }

    public static Collection<? extends String> listEC2Instances(Ec2Client ec2) {
        Collection<String> instanceIdList = new ArrayList<>();
        String nextToken = null;

        try {
            do {
                DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
                DescribeInstancesResponse response = ec2.describeInstances(request);

                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
                        System.out.printf(
                                "Found Reservation with id %s, " +
                                        "AMI %s, " +
                                        "type %s, " +
                                        "state %s " +
                                        "and monitoring state %s" +
                                        "tags %s" +
                                        "\n",
                                instance.instanceId(),
                                instance.imageId(),
                                instance.instanceType(),
                                instance.state().name(),
                                instance.monitoring().state(),
                                instance.tags());

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
}
