/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.S3ResourceType;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public final class S3OutpostResource
implements S3Resource {
    private final String partition;
    private final String region;
    private final String accountId;
    private final String outpostId;

    private S3OutpostResource(Builder b) {
        this.partition = ValidationUtils.assertStringNotEmpty(b.partition, "partition");
        this.region = ValidationUtils.assertStringNotEmpty(b.region, "region");
        this.accountId = ValidationUtils.assertStringNotEmpty(b.accountId, "accountId");
        this.outpostId = ValidationUtils.assertStringNotEmpty(b.outpostId, "outpostId");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getType() {
        return S3ResourceType.OUTPOST.toString();
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

    public String getOutpostId() {
        return this.outpostId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3OutpostResource that = (S3OutpostResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.outpostId.equals(that.outpostId);
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.outpostId.hashCode();
        return result;
    }

    public static final class Builder {
        private String outpostId;
        private String partition;
        private String region;
        private String accountId;

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

        public Builder withOutpostId(String outpostId) {
            this.outpostId = outpostId;
            return this;
        }

        public S3OutpostResource build() {
            return new S3OutpostResource(this);
        }
    }
}

