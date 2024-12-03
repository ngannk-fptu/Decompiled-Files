/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aws;

import com.hazelcast.aws.PortRange;

public final class AwsConfig {
    private final String region;
    private final String hostHeader;
    private final String securityGroupName;
    private final String tagKey;
    private final String tagValue;
    private final int connectionTimeoutSeconds;
    private final int connectionRetries;
    private final PortRange hzPort;
    private String accessKey;
    private String secretKey;
    private String iamRole;

    private AwsConfig(String accessKey, String secretKey, String region, String iamRole, String hostHeader, String securityGroupName, String tagKey, String tagValue, int connectionTimeoutSeconds, int connectionRetries, PortRange hzPort) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.iamRole = iamRole;
        this.hostHeader = hostHeader;
        this.securityGroupName = securityGroupName;
        this.tagKey = tagKey;
        this.tagValue = tagValue;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
        this.connectionRetries = connectionRetries;
        this.hzPort = hzPort;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    @Deprecated
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    @Deprecated
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return this.region;
    }

    public String getIamRole() {
        return this.iamRole;
    }

    @Deprecated
    public void setIamRole(String iamRole) {
        this.iamRole = iamRole;
    }

    public String getHostHeader() {
        return this.hostHeader;
    }

    public String getSecurityGroupName() {
        return this.securityGroupName;
    }

    public String getTagKey() {
        return this.tagKey;
    }

    public String getTagValue() {
        return this.tagValue;
    }

    public int getConnectionTimeoutSeconds() {
        return this.connectionTimeoutSeconds;
    }

    public int getConnectionRetries() {
        return this.connectionRetries;
    }

    public PortRange getHzPort() {
        return this.hzPort;
    }

    public String toString() {
        return "AwsConfig{accessKey='***', secretKey='***', region='" + this.region + '\'' + ", iamRole='" + this.iamRole + '\'' + ", hostHeader='" + this.hostHeader + '\'' + ", securityGroupName='" + this.securityGroupName + '\'' + ", tagKey='" + this.tagKey + '\'' + ", tagValue='" + this.tagValue + '\'' + ", connectionTimeoutSeconds=" + this.connectionTimeoutSeconds + ", connectionRetries=" + this.connectionRetries + ", hzPort=" + this.hzPort + '}';
    }

    public static class Builder {
        private String accessKey;
        private String secretKey;
        private String region;
        private String iamRole;
        private String hostHeader;
        private String securityGroupName;
        private String tagKey;
        private String tagValue;
        private int connectionTimeoutSeconds;
        private int connectionRetries;
        private PortRange hzPort;

        public Builder setAccessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public Builder setSecretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder setRegion(String region) {
            this.region = region;
            return this;
        }

        public Builder setIamRole(String iamRole) {
            this.iamRole = iamRole;
            return this;
        }

        public Builder setHostHeader(String hostHeader) {
            this.hostHeader = hostHeader;
            return this;
        }

        public Builder setSecurityGroupName(String securityGroupName) {
            this.securityGroupName = securityGroupName;
            return this;
        }

        public Builder setTagKey(String tagKey) {
            this.tagKey = tagKey;
            return this;
        }

        public Builder setTagValue(String tagValue) {
            this.tagValue = tagValue;
            return this;
        }

        public Builder setConnectionTimeoutSeconds(int connectionTimeoutSeconds) {
            this.connectionTimeoutSeconds = connectionTimeoutSeconds;
            return this;
        }

        public Builder setConnectionRetries(int connectionRetries) {
            this.connectionRetries = connectionRetries;
            return this;
        }

        public Builder setHzPort(PortRange hzPort) {
            this.hzPort = hzPort;
            return this;
        }

        public AwsConfig build() {
            return new AwsConfig(this.accessKey, this.secretKey, this.region, this.iamRole, this.hostHeader, this.securityGroupName, this.tagKey, this.tagValue, this.connectionTimeoutSeconds, this.connectionRetries, this.hzPort);
        }
    }
}

