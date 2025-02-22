/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.InvalidConfigurationException
 */
package com.hazelcast.aws;

import com.hazelcast.aws.AwsConfig;
import com.hazelcast.aws.impl.DescribeInstances;
import com.hazelcast.aws.utility.MetadataUtil;
import com.hazelcast.config.InvalidConfigurationException;
import java.util.Collection;
import java.util.Map;

public class AWSClient {
    private final AwsConfig awsConfig;
    private String endpoint;

    public AWSClient(AwsConfig awsConfig) {
        if (awsConfig == null) {
            throw new IllegalArgumentException("AwsConfig is required!");
        }
        this.awsConfig = awsConfig;
        this.endpoint = awsConfig.getHostHeader();
        if (awsConfig.getRegion() != null && awsConfig.getRegion().length() > 0) {
            if (!awsConfig.getHostHeader().startsWith("ec2.")) {
                throw new InvalidConfigurationException("HostHeader should start with \"ec2.\" prefix");
            }
            this.setEndpoint(awsConfig.getHostHeader().replace("ec2.", "ec2." + awsConfig.getRegion() + "."));
        }
    }

    public Collection<String> getPrivateIpAddresses() throws Exception {
        Map<String, String> result = new DescribeInstances(this.awsConfig, this.endpoint).execute();
        return result.keySet();
    }

    public Map<String, String> getAddresses() throws Exception {
        return new DescribeInstances(this.awsConfig, this.endpoint).execute();
    }

    public String getAvailabilityZone() {
        String uri = "http://169.254.169.254/latest/meta-data/".concat("placement/availability-zone/");
        return MetadataUtil.retrieveMetadataFromURI(uri, this.awsConfig.getConnectionTimeoutSeconds(), this.awsConfig.getConnectionRetries());
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String s) {
        this.endpoint = s;
    }
}

