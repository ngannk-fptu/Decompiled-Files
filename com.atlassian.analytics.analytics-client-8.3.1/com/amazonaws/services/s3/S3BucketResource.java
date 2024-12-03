/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.S3ResourceType;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public final class S3BucketResource
implements S3Resource {
    private static final S3ResourceType S3_RESOURCE_TYPE = S3ResourceType.BUCKET;
    private final String partition;
    private final String region;
    private final String accountId;
    private final String bucketName;

    private S3BucketResource(Builder b) {
        this.bucketName = ValidationUtils.assertStringNotEmpty(b.bucketName, "bucketName");
        this.partition = b.partition;
        this.region = b.region;
        this.accountId = b.accountId;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getType() {
        return S3_RESOURCE_TYPE.toString();
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

    public String getBucketName() {
        return this.bucketName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3BucketResource that = (S3BucketResource)o;
        if (this.partition != null ? !this.partition.equals(that.partition) : that.partition != null) {
            return false;
        }
        if (this.region != null ? !this.region.equals(that.region) : that.region != null) {
            return false;
        }
        if (this.accountId != null ? !this.accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return this.bucketName.equals(that.bucketName);
    }

    public int hashCode() {
        int result = this.partition != null ? this.partition.hashCode() : 0;
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        result = 31 * result + (this.accountId != null ? this.accountId.hashCode() : 0);
        result = 31 * result + this.bucketName.hashCode();
        return result;
    }

    public static final class Builder {
        private String partition;
        private String region;
        private String accountId;
        private String bucketName;

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public Builder withPartition(String partition) {
            this.setPartition(partition);
            return this;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public Builder withRegion(String region) {
            this.setRegion(region);
            return this;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public Builder withAccountId(String accountId) {
            this.setAccountId(accountId);
            return this;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public Builder withBucketName(String bucketName) {
            this.setBucketName(bucketName);
            return this;
        }

        public S3BucketResource build() {
            return new S3BucketResource(this);
        }
    }
}

