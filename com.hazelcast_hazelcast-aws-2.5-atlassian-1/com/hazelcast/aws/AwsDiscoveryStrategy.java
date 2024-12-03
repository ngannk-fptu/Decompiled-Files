/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.InvalidConfigurationException
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  com.hazelcast.nio.Address
 *  com.hazelcast.spi.discovery.AbstractDiscoveryStrategy
 *  com.hazelcast.spi.discovery.DiscoveryNode
 *  com.hazelcast.spi.discovery.SimpleDiscoveryNode
 *  com.hazelcast.util.StringUtil
 */
package com.hazelcast.aws;

import com.hazelcast.aws.AWSClient;
import com.hazelcast.aws.AwsConfig;
import com.hazelcast.aws.AwsProperties;
import com.hazelcast.aws.PortRange;
import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AwsDiscoveryStrategy
extends AbstractDiscoveryStrategy {
    private static final ILogger LOGGER = Logger.getLogger(AwsDiscoveryStrategy.class);
    private static final String DEFAULT_PORT_RANGE = "5701-5708";
    private static final Integer DEFAULT_CONNECTION_RETRIES = 10;
    private static final int DEFAULT_CONNECTION_TIMEOUT_SECONDS = 10;
    private static final String DEFAULT_REGION = "us-east-1";
    private static final String DEFAULT_HOST_HEADER = "ec2.amazonaws.com";
    private final AwsConfig awsConfig;
    private final AWSClient awsClient;
    private final Map<String, Object> memberMetadata = new HashMap<String, Object>();

    public AwsDiscoveryStrategy(Map<String, Comparable> properties) {
        super(LOGGER, properties);
        this.awsConfig = this.getAwsConfig();
        try {
            this.awsClient = new AWSClient(this.awsConfig);
        }
        catch (IllegalArgumentException e) {
            throw new InvalidConfigurationException("AWS configuration is not valid", (Throwable)e);
        }
    }

    AwsDiscoveryStrategy(Map<String, Comparable> properties, AWSClient client) {
        super(LOGGER, properties);
        this.awsConfig = this.getAwsConfig();
        this.awsClient = client;
    }

    private AwsConfig getAwsConfig() throws IllegalArgumentException {
        AwsConfig config = AwsConfig.builder().setAccessKey(this.getOrNull(AwsProperties.ACCESS_KEY)).setSecretKey(this.getOrNull(AwsProperties.SECRET_KEY)).setRegion((String)((Object)this.getOrDefault(AwsProperties.REGION.getDefinition(), (Comparable)((Object)DEFAULT_REGION)))).setIamRole(this.getOrNull(AwsProperties.IAM_ROLE)).setHostHeader((String)((Object)this.getOrDefault(AwsProperties.HOST_HEADER.getDefinition(), (Comparable)((Object)DEFAULT_HOST_HEADER)))).setSecurityGroupName(this.getOrNull(AwsProperties.SECURITY_GROUP_NAME)).setTagKey(this.getOrNull(AwsProperties.TAG_KEY)).setTagValue(this.getOrNull(AwsProperties.TAG_VALUE)).setConnectionTimeoutSeconds((Integer)this.getOrDefault(AwsProperties.CONNECTION_TIMEOUT_SECONDS.getDefinition(), Integer.valueOf(10))).setConnectionRetries((Integer)this.getOrDefault(AwsProperties.CONNECTION_RETRIES.getDefinition(), DEFAULT_CONNECTION_RETRIES)).setHzPort(new PortRange(this.getPortRange())).build();
        this.reviewConfiguration(config);
        return config;
    }

    private String getPortRange() {
        Comparable portRange = this.getOrNull(AwsProperties.PORT.getDefinition());
        if (portRange == null) {
            return DEFAULT_PORT_RANGE;
        }
        return portRange.toString();
    }

    private void reviewConfiguration(AwsConfig config) {
        if (StringUtil.isNullOrEmptyAfterTrim((String)config.getSecretKey()) || StringUtil.isNullOrEmptyAfterTrim((String)config.getAccessKey())) {
            if (!StringUtil.isNullOrEmptyAfterTrim((String)config.getIamRole())) {
                this.getLogger().info("Describe instances will be queried with iam-role, please make sure given iam-role have ec2:DescribeInstances policy attached.");
            } else {
                this.getLogger().warning("Describe instances will be queried with iam-role assigned to EC2 instance, please make sure given iam-role have ec2:DescribeInstances policy attached.");
            }
        } else if (!StringUtil.isNullOrEmptyAfterTrim((String)config.getIamRole())) {
            this.getLogger().info("No need to define iam-role, when access and secret keys are configured!");
        }
    }

    public Map<String, Object> discoverLocalMetadata() {
        if (this.memberMetadata.isEmpty()) {
            this.memberMetadata.put("hazelcast.partition.group.zone", this.awsClient.getAvailabilityZone());
        }
        return this.memberMetadata;
    }

    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            Map<String, String> privatePublicIpAddressPairs = this.awsClient.getAddresses();
            if (privatePublicIpAddressPairs.isEmpty()) {
                this.getLogger().warning("No EC2 instances found!");
                return Collections.emptyList();
            }
            if (this.getLogger().isFinestEnabled()) {
                StringBuilder sb = new StringBuilder("Found the following EC2 instances:\n");
                for (Map.Entry<String, String> entry : privatePublicIpAddressPairs.entrySet()) {
                    sb.append("    ").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                }
                this.getLogger().finest(sb.toString());
            }
            ArrayList<DiscoveryNode> nodes = new ArrayList<DiscoveryNode>(privatePublicIpAddressPairs.size());
            for (Map.Entry<String, String> entry : privatePublicIpAddressPairs.entrySet()) {
                for (int port = this.awsConfig.getHzPort().getFromPort(); port <= this.awsConfig.getHzPort().getToPort(); ++port) {
                    nodes.add((DiscoveryNode)new SimpleDiscoveryNode(new Address(entry.getKey(), port), new Address(entry.getValue(), port)));
                }
            }
            return nodes;
        }
        catch (Exception e) {
            LOGGER.warning("Cannot discover nodes, returning empty list", (Throwable)e);
            return Collections.emptyList();
        }
    }

    private String getOrNull(AwsProperties awsProperties) {
        return (String)((Object)this.getOrNull(awsProperties.getDefinition()));
    }
}

