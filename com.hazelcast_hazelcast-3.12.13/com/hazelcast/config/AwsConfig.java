/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AliasedDiscoveryConfig;
import com.hazelcast.util.Preconditions;

public class AwsConfig
extends AliasedDiscoveryConfig<AwsConfig> {
    private static final int CONNECTION_TIMEOUT = 5;

    public AwsConfig() {
        super("aws");
    }

    public AwsConfig(AwsConfig awsConfig) {
        super(awsConfig);
    }

    @Deprecated
    public String getAccessKey() {
        return this.getProperties().get("access-key");
    }

    @Deprecated
    public AwsConfig setAccessKey(String accessKey) {
        this.getProperties().put("access-key", Preconditions.checkHasText(accessKey, "accessKey must contain text"));
        return this;
    }

    @Deprecated
    public String getSecretKey() {
        return this.getProperties().get("secret-key");
    }

    @Deprecated
    public AwsConfig setSecretKey(String secretKey) {
        this.getProperties().put("secret-key", Preconditions.checkHasText(secretKey, "secretKey must contain text"));
        return this;
    }

    @Deprecated
    public String getRegion() {
        return this.getProperties().get("region");
    }

    @Deprecated
    public AwsConfig setRegion(String region) {
        this.getProperties().put("region", Preconditions.checkHasText(region, "region must contain text"));
        return this;
    }

    @Deprecated
    public String getHostHeader() {
        return this.getProperties().get("host-header");
    }

    @Deprecated
    public AwsConfig setHostHeader(String hostHeader) {
        this.getProperties().put("host-header", Preconditions.checkHasText(hostHeader, "hostHeader must contain text"));
        return this;
    }

    @Deprecated
    public String getSecurityGroupName() {
        return this.getProperties().get("security-group-name");
    }

    @Deprecated
    public AwsConfig setSecurityGroupName(String securityGroupName) {
        this.getProperties().put("security-group-name", securityGroupName);
        return this;
    }

    @Deprecated
    public String getTagKey() {
        return this.getProperties().get("tag-key");
    }

    public AwsConfig setTagKey(String tagKey) {
        this.getProperties().put("tag-key", tagKey);
        return this;
    }

    @Deprecated
    public String getTagValue() {
        return this.getProperties().get("tag-value");
    }

    @Deprecated
    public AwsConfig setTagValue(String tagValue) {
        this.getProperties().put("tag-value", tagValue);
        return this;
    }

    @Deprecated
    public int getConnectionTimeoutSeconds() {
        if (!this.getProperties().containsKey("connection-timeout-seconds")) {
            return 5;
        }
        return Integer.parseInt(this.getProperties().get("connection-timeout-seconds"));
    }

    @Deprecated
    public AwsConfig setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
        if (connectionTimeoutSeconds < 0) {
            throw new IllegalArgumentException("connection timeout can't be smaller than 0");
        }
        this.getProperties().put("connection-timeout-seconds", String.valueOf(connectionTimeoutSeconds));
        return this;
    }

    @Deprecated
    public String getIamRole() {
        return this.getProperties().get("iam-role");
    }

    @Deprecated
    public AwsConfig setIamRole(String iamRole) {
        this.getProperties().put("iam-role", iamRole);
        return this;
    }

    @Deprecated
    public String getHzPort() {
        return this.getProperties().get("hz-port");
    }

    @Override
    public AwsConfig setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        return this;
    }

    @Override
    public int getId() {
        return 60;
    }
}

