/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.S3ResourceType;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public final class S3ObjectLambdasResource
implements S3Resource {
    private final String partition;
    private final String region;
    private final String accountId;
    private final String accessPointName;

    private S3ObjectLambdasResource(Builder b) {
        this.partition = ValidationUtils.assertStringNotEmpty(b.partition, "partition");
        this.region = ValidationUtils.assertStringNotEmpty(b.region, "region");
        this.accountId = ValidationUtils.assertStringNotEmpty(b.accountId, "accountId");
        this.accessPointName = ValidationUtils.assertStringNotEmpty(b.accessPointName, "accessPointName");
    }

    @Override
    public String getType() {
        return S3ResourceType.OBJECT_LAMBDAS.toString();
    }

    @Override
    public S3Resource getParentS3Resource() {
        return null;
    }

    @Override
    public String getPartition() {
        return this.partition;
    }

    @Override
    public String getRegion() {
        return this.region;
    }

    @Override
    public String getAccountId() {
        return this.accountId;
    }

    public String getAccessPointName() {
        return this.accessPointName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3ObjectLambdasResource that = (S3ObjectLambdasResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.accessPointName != null ? this.accessPointName.equals(that.accessPointName) : that.accessPointName == null;
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + (this.accessPointName != null ? this.accessPointName.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String partition;
        private String region;
        private String accountId;
        private String accessPointName;

        private Builder() {
        }

        public Builder withPartition(String partition) {
            this.partition = partition;
            return this;
        }

        public Builder withRegion(String region) {
            this.region = region;
            return this;
        }

        public Builder withAccountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder withAccessPointName(String accessPointName) {
            this.accessPointName = accessPointName;
            return this;
        }

        public S3ObjectLambdasResource build() {
            return new S3ObjectLambdasResource(this);
        }
    }
}

