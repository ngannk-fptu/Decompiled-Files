/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.S3Resource;
import com.amazonaws.services.s3.S3ResourceType;
import com.amazonaws.util.ValidationUtils;

@SdkInternalApi
public final class S3ObjectResource
implements S3Resource {
    private static final S3ResourceType S3_RESOURCE_TYPE = S3ResourceType.OBJECT;
    private final S3Resource parentS3Resource;
    private final String key;

    private S3ObjectResource(Builder b) {
        this.parentS3Resource = this.validateParentS3Resource(b.parentS3Resource);
        this.key = ValidationUtils.assertStringNotEmpty(b.key, "key");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getType() {
        return S3_RESOURCE_TYPE.toString();
    }

    @Override
    public String getPartition() {
        return this.parentS3Resource.getPartition();
    }

    @Override
    public String getRegion() {
        return this.parentS3Resource.getRegion();
    }

    @Override
    public String getAccountId() {
        return this.parentS3Resource.getAccountId();
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public S3Resource getParentS3Resource() {
        return this.parentS3Resource;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3ObjectResource that = (S3ObjectResource)o;
        if (this.parentS3Resource != null ? !this.parentS3Resource.equals(that.parentS3Resource) : that.parentS3Resource != null) {
            return false;
        }
        return this.key != null ? this.key.equals(that.key) : that.key == null;
    }

    public int hashCode() {
        int result = this.parentS3Resource != null ? this.parentS3Resource.hashCode() : 0;
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        return result;
    }

    private S3Resource validateParentS3Resource(S3Resource parentS3Resource) {
        ValidationUtils.assertNotNull(parentS3Resource, "parentS3Resource");
        if (!S3ResourceType.ACCESS_POINT.toString().equals(parentS3Resource.getType()) && !S3ResourceType.BUCKET.toString().equals(parentS3Resource.getType())) {
            throw new IllegalArgumentException("Invalid 'parentS3Resource' type. An S3 object resource must be associated with either a bucket or access-point parent resource.");
        }
        return parentS3Resource;
    }

    public static final class Builder {
        private S3Resource parentS3Resource;
        private String key;

        public void setParentS3Resource(S3Resource parentS3Resource) {
            this.parentS3Resource = parentS3Resource;
        }

        public Builder withParentS3Resource(S3Resource parentS3Resource) {
            this.setParentS3Resource(parentS3Resource);
            return this;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Builder withKey(String key) {
            this.setKey(key);
            return this;
        }

        public S3ObjectResource build() {
            return new S3ObjectResource(this);
        }
    }
}

