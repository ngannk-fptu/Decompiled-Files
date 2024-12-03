/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;

public class CloudPlatformMetadata {
    private final CloudPlatformType cloudPlatform;
    private final String instanceType;

    private CloudPlatformMetadata(Builder builder) {
        this.cloudPlatform = builder.cloudPlatform;
        this.instanceType = builder.instanceType;
    }

    public CloudPlatformType getCloudPlatform() {
        return this.cloudPlatform;
    }

    public String getInstanceType() {
        return this.instanceType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CloudPlatformType cloudPlatform;
        private String instanceType;

        private Builder() {
        }

        public Builder cloudPlatform(CloudPlatformType cloudPlatform) {
            this.cloudPlatform = cloudPlatform;
            return this;
        }

        public Builder instanceType(String instanceType) {
            this.instanceType = instanceType;
            return this;
        }

        public CloudPlatformMetadata build() {
            return new CloudPlatformMetadata(this);
        }
    }
}

