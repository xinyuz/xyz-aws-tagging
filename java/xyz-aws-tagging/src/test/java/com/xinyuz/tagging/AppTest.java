package com.xinyuz.tagging;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    private static Ec2Client ec2;

    // Define the data members required for the tests
    private static String instanceId   = ""; // gets set in test 2
    private static String ami          = "";
    private static String instanceName = "";
    private static String keyName      = "";
    private static String groupName    = "";
    private static String groupDesc    = "";
    private static String groupId      = "";
    private static String vpcId        = "";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        ec2 = Ec2Client.builder()
                .region(region)
                .build();

        //        try (InputStream input = AWSEC2ServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {
        //
        //            Properties prop = new Properties();
        //
        //            if (input == null) {
        //                System.out.println("Sorry, unable to find config.properties");
        //                return;
        //            }
        //
        //            //load a properties file from class path, inside static method
        //            prop.load(input);
        //            ami = prop.getProperty("ami");
        //            instanceName = prop.getProperty("instanceName");
        //            keyName = prop.getProperty("keyPair");
        //            groupName = prop.getProperty("groupName");
        //            groupDesc = prop.getProperty("groupDesc");
        //            vpcId = prop.getProperty("vpcId");
        //
        //        } catch (IOException ex) {
        //            ex.printStackTrace();
        //        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(ec2);
        System.out.println("Test 1 passed");
    }

}
